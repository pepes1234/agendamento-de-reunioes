package br.edu.agendamento.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.edu.agendamento.config.DatabaseConnection;
import br.edu.agendamento.model.Convidado;
import br.edu.agendamento.model.ParticipanteResposta;
import br.edu.agendamento.model.ResumoOpcao;

public class ConvidadoRepository {
    public int contarRespostasConfirmadas(long reuniaoId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM convidado "
                     + "WHERE reuniao_id = ? AND confirmado = TRUE")) {
            statement.setLong(1, reuniaoId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt(1) : 0;
            }
        }
    }

    public void atualizarNome(long convidadoId, String nome) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE convidado SET nome = NULLIF(?, '') WHERE id = ?")) {
            statement.setString(1, nome == null ? "" : nome.trim());
            statement.setLong(2, convidadoId);
            statement.executeUpdate();
        }
    }

    public Convidado inserir(long reuniaoId, String email) throws SQLException {
        String sql = "INSERT INTO convidado (reuniao_id, email, token_resposta) VALUES (?, ?, UUID())";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, reuniaoId);
            statement.setString(2, email);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) {
                    throw new SQLException("Nao foi possivel obter o convidado criado.");
                }
                long id = keys.getLong(1);
                Convidado convidado = buscarPorId(id);
                return convidado;
            }
        }
    }

    public Convidado buscarPorId(long id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, reuniao_id, nome, email, token_resposta, confirmado "
                     + "FROM convidado WHERE id = ?")) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        }
    }

    public Convidado buscarPorToken(String token) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, reuniao_id, nome, email, token_resposta, confirmado "
                     + "FROM convidado WHERE token_resposta = ?")) {
            statement.setString(1, token);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        }
    }

    public List<Convidado> listarPorReuniao(long reuniaoId) throws SQLException {
        String sql = "SELECT c.id, c.reuniao_id, c.nome, c.email, c.token_resposta, c.confirmado, "
                + "COUNT(d.opcao_id) AS quantidade "
                + "FROM convidado c LEFT JOIN disponibilidade d ON d.convidado_id = c.id "
                + "WHERE c.reuniao_id = ? GROUP BY c.id, c.reuniao_id, c.nome, c.email, "
                + "c.token_resposta, c.confirmado ORDER BY c.email";
        List<Convidado> convidados = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reuniaoId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Convidado convidado = mapear(result);
                    convidado.setQuantidadeDisponibilidades(result.getInt("quantidade"));
                    convidados.add(convidado);
                }
            }
        }
        return convidados;
    }

    public void salvarResposta(long convidadoId, Map<Long, String> respostas) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement limpar = connection.prepareStatement(
                         "DELETE FROM disponibilidade WHERE convidado_id = ?");
                 PreparedStatement inserir = connection.prepareStatement(
                         "INSERT INTO disponibilidade (convidado_id, opcao_id, nivel) VALUES (?, ?, ?)")) {
                limpar.setLong(1, convidadoId);
                limpar.executeUpdate();

                for (Map.Entry<Long, String> resposta : respostas.entrySet()) {
                    inserir.setLong(1, convidadoId);
                    inserir.setLong(2, resposta.getKey());
                    inserir.setString(3, resposta.getValue());
                    inserir.addBatch();
                }
                inserir.executeBatch();
                try (PreparedStatement confirmar = connection.prepareStatement(
                        "UPDATE convidado SET confirmado = TRUE, respondido_em = ? WHERE id = ?")) {
                    confirmar.setTimestamp(1, Timestamp.from(java.time.Instant.now()));
                    confirmar.setLong(2, convidadoId);
                    confirmar.executeUpdate();
                }
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public Map<Long, String> listarRespostas(long convidadoId) throws SQLException {
        Map<Long, String> respostas = new LinkedHashMap<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT opcao_id, nivel FROM disponibilidade WHERE convidado_id = ?")) {
            statement.setLong(1, convidadoId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    respostas.put(result.getLong("opcao_id"), result.getString("nivel"));
                }
            }
        }
        return respostas;
    }

    public List<ResumoOpcao> listarResumoRespostas(long reuniaoId) throws SQLException {
        String sql = "SELECT o.id, o.data_opcao, o.turno, o.hora_inicio, o.hora_fim, "
                + "SUM(CASE WHEN d.nivel = 'DISPONIVEL' THEN 1 ELSE 0 END) AS disponivel, "
                + "SUM(CASE WHEN d.nivel = 'TALVEZ' THEN 1 ELSE 0 END) AS talvez, "
                + "SUM(CASE WHEN d.nivel = 'INDISPONIVEL' THEN 1 ELSE 0 END) AS indisponivel "
                + "FROM opcao_agendamento o "
                + "LEFT JOIN disponibilidade d ON d.opcao_id = o.id "
                + "WHERE o.reuniao_id = ? "
                + "GROUP BY o.id, o.data_opcao, o.turno, o.hora_inicio, o.hora_fim "
                + "ORDER BY o.data_opcao, o.hora_inicio, o.turno";
        List<ResumoOpcao> resumo = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reuniaoId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    ResumoOpcao item = new ResumoOpcao();
                    item.setOpcaoId(result.getLong("id"));
                    item.setDataOpcao(result.getObject("data_opcao", LocalDate.class));
                    item.setTurno(result.getString("turno"));
                    Time inicio = result.getTime("hora_inicio");
                    Time fim = result.getTime("hora_fim");
                    item.setHoraInicio(inicio == null ? null : inicio.toLocalTime());
                    item.setHoraFim(fim == null ? null : fim.toLocalTime());
                    item.setDisponivel(result.getInt("disponivel"));
                    item.setTalvez(result.getInt("talvez"));
                    item.setIndisponivel(result.getInt("indisponivel"));
                    resumo.add(item);
                }
            }
        }
        Map<Long, ResumoOpcao> porOpcao = new HashMap<>();
        for (ResumoOpcao item : resumo) {
            porOpcao.put(item.getOpcaoId(), item);
        }
        String participantesSql = "SELECT d.opcao_id, d.nivel, c.token_resposta, "
                + "COALESCE(NULLIF(TRIM(c.nome), ''), c.email) AS participante "
                + "FROM disponibilidade d "
                + "JOIN convidado c ON c.id = d.convidado_id "
                + "JOIN opcao_agendamento o ON o.id = d.opcao_id "
                + "WHERE o.reuniao_id = ? "
                + "ORDER BY participante";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(participantesSql)) {
            statement.setLong(1, reuniaoId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    ResumoOpcao item = porOpcao.get(result.getLong("opcao_id"));
                    if (item == null) {
                        continue;
                    }
                    String participante = result.getString("participante");
                    ParticipanteResposta pessoa = new ParticipanteResposta(
                            participante, result.getString("token_resposta"));
                    String nivel = result.getString("nivel");
                    if ("DISPONIVEL".equals(nivel)) {
                        item.getDisponiveisPorNome().add(pessoa);
                    } else if ("TALVEZ".equals(nivel)) {
                        item.getTalvezPorNome().add(pessoa);
                    }
                }
            }
        }
        return resumo;
    }

    public void registrarNotificacao(long convidadoId, String assunto, String status, String erro)
            throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO notificacao_email "
                     + "(convidado_id, assunto, status, tentativas, enviado_em, ultimo_erro) "
                     + "VALUES (?, ?, ?, 1, ?, ?)")) {
            statement.setLong(1, convidadoId);
            statement.setString(2, assunto);
            statement.setString(3, status);
            statement.setTimestamp(4, "ENVIADO".equals(status)
                    ? Timestamp.from(java.time.Instant.now()) : null);
            statement.setString(5, erro);
            statement.executeUpdate();
        }
    }

    private Convidado mapear(ResultSet result) throws SQLException {
        Convidado convidado = new Convidado();
        convidado.setId(result.getLong("id"));
        convidado.setReuniaoId(result.getLong("reuniao_id"));
        convidado.setNome(result.getString("nome"));
        convidado.setEmail(result.getString("email"));
        convidado.setTokenResposta(result.getString("token_resposta"));
        convidado.setConfirmado(result.getBoolean("confirmado"));
        return convidado;
    }
}
