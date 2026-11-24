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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import br.edu.agendamento.config.DatabaseConnection;
import br.edu.agendamento.model.Participante;
import br.edu.agendamento.model.ParticipanteResposta;
import br.edu.agendamento.model.ResumoHorario;

public class ParticipanteRepository {
    public Participante inserir(long reuniaoId, String nome, String email, boolean proponente) throws SQLException {
        String sql = "INSERT INTO participante (reuniao_id, nome, email, token, proponente) "
                + "VALUES (?, NULLIF(?, ''), ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, reuniaoId);
            statement.setString(2, nome == null ? "" : nome.trim());
            statement.setString(3, email);
            statement.setString(4, java.util.UUID.randomUUID().toString());
            statement.setBoolean(5, proponente);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (!keys.next()) throw new SQLException("Nao foi possivel obter o participante criado.");
                return buscarPorId(keys.getLong(1));
            }
        }
    }

    public Participante buscarPorId(long id) throws SQLException {
        return mapearUnico("id", id);
    }

    public Participante buscarPorToken(String token) throws SQLException {
        return mapearUnico("token", token);
    }

    private Participante mapearUnico(String coluna, Object valor) throws SQLException {
        String sql = "SELECT id, reuniao_id, nome, email, token, proponente "
                + "FROM participante WHERE " + coluna + " = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, valor);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapear(result) : null;
            }
        }
    }

    public List<Participante> listarPorReuniao(long reuniaoId) throws SQLException {
        String sql = "SELECT p.id, p.reuniao_id, p.nome, p.email, p.token, p.proponente, "
                + "COUNT(d.id) AS quantidade, p.respondido_em "
                + "FROM participante p LEFT JOIN disponibilidade d ON d.participante_id = p.id "
                + "WHERE p.reuniao_id = ? GROUP BY p.id, p.reuniao_id, p.nome, p.email, p.token, "
                + "p.proponente, p.respondido_em ORDER BY p.email";
        List<Participante> participantes = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reuniaoId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Participante participante = mapear(result);
                    participante.setQuantidadeDisponibilidades(result.getInt("quantidade"));
                    Timestamp respondido = result.getTimestamp("respondido_em");
                    participante.setRespondidoEm(respondido == null ? null : respondido.toLocalDateTime());
                    participantes.add(participante);
                }
            }
        }
        return participantes;
    }

    public int contarRespondentes(long reuniaoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM participante "
                + "WHERE reuniao_id = ? AND respondido_em IS NOT NULL";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reuniaoId);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? result.getInt(1) : 0;
            }
        }
    }

    public void atualizarNome(long participanteId, String nome) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE participante SET nome = NULLIF(?, '') WHERE id = ?")) {
            statement.setString(1, nome == null ? "" : nome.trim());
            statement.setLong(2, participanteId);
            statement.executeUpdate();
        }
    }

    public void salvarResposta(long participanteId, Map<Long, String> respostas) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement limpar = connection.prepareStatement(
                         "DELETE FROM disponibilidade WHERE participante_id = ?");
                 PreparedStatement inserir = connection.prepareStatement(
                         "INSERT INTO disponibilidade (participante_id, horario_id, resposta) VALUES (?, ?, ?)")) {
                limpar.setLong(1, participanteId);
                limpar.executeUpdate();
                for (Map.Entry<Long, String> resposta : respostas.entrySet()) {
                    inserir.setLong(1, participanteId);
                    inserir.setLong(2, resposta.getKey());
                    inserir.setString(3, resposta.getValue());
                    inserir.addBatch();
                }
                inserir.executeBatch();
                try (PreparedStatement responder = connection.prepareStatement(
                        "UPDATE participante SET respondido_em = ? WHERE id = ?")) {
                    responder.setTimestamp(1, Timestamp.from(java.time.Instant.now()));
                    responder.setLong(2, participanteId);
                    responder.executeUpdate();
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

    public Map<Long, String> listarRespostas(long participanteId) throws SQLException {
        Map<Long, String> respostas = new LinkedHashMap<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT horario_id, resposta FROM disponibilidade WHERE participante_id = ?")) {
            statement.setLong(1, participanteId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) respostas.put(result.getLong("horario_id"), result.getString("resposta"));
            }
        }
        return respostas;
    }

    public List<ResumoHorario> listarResumo(long reuniaoId) throws SQLException {
        String sql = "SELECT h.id, h.dia, h.periodo, h.hora_inicio, h.hora_fim, "
                + "SUM(CASE WHEN d.resposta = 'SIM' THEN 1 ELSE 0 END) AS sim, "
                + "SUM(CASE WHEN d.resposta = 'TALVEZ' THEN 1 ELSE 0 END) AS talvez "
                + "FROM horario h LEFT JOIN disponibilidade d ON d.horario_id = h.id "
                + "WHERE h.reuniao_id = ? "
                + "GROUP BY h.id, h.dia, h.periodo, h.hora_inicio, h.hora_fim "
                + "ORDER BY h.dia, h.hora_inicio, h.periodo";
        List<ResumoHorario> resumo = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reuniaoId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    ResumoHorario item = new ResumoHorario();
                    item.setHorarioId(result.getLong("id"));
                    item.setDia(result.getObject("dia", LocalDate.class));
                    item.setPeriodo(result.getString("periodo"));
                    Time inicio = result.getTime("hora_inicio");
                    Time fim = result.getTime("hora_fim");
                    item.setHoraInicio(inicio == null ? null : inicio.toLocalTime());
                    item.setHoraFim(fim == null ? null : fim.toLocalTime());
                    item.setDisponivel(result.getInt("sim"));
                    item.setTalvez(result.getInt("talvez"));
                    resumo.add(item);
                }
            }
        }
        String participantesSql = "SELECT d.horario_id, d.resposta, p.token, "
                + "COALESCE(NULLIF(TRIM(p.nome), ''), p.email) AS participante "
                + "FROM disponibilidade d "
                + "JOIN participante p ON p.id = d.participante_id "
                + "JOIN horario h ON h.id = d.horario_id "
                + "WHERE h.reuniao_id = ? ORDER BY participante";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(participantesSql)) {
            statement.setLong(1, reuniaoId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    long horarioId = result.getLong("horario_id");
                    ResumoHorario item = resumo.stream()
                            .filter(r -> r.getHorarioId() == horarioId)
                            .findFirst().orElse(null);
                    if (item == null) continue;
                    ParticipanteResposta pessoa =
                            new ParticipanteResposta(result.getString("participante"), result.getString("token"));
                    if ("SIM".equals(result.getString("resposta"))) item.getDisponiveisPorNome().add(pessoa);
                    else if ("TALVEZ".equals(result.getString("resposta"))) item.getTalvezPorNome().add(pessoa);
                }
            }
        }
        return resumo;
    }

    public void registrarEmailEnviado(long participanteId) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE participante SET email_enviado_em = ? WHERE id = ?")) {
            statement.setTimestamp(1, Timestamp.from(java.time.Instant.now()));
            statement.setLong(2, participanteId);
            statement.executeUpdate();
        }
    }

    private Participante mapear(ResultSet result) throws SQLException {
        Participante participante = new Participante();
        participante.setId(result.getLong("id"));
        participante.setReuniaoId(result.getLong("reuniao_id"));
        participante.setNome(result.getString("nome"));
        participante.setEmail(result.getString("email"));
        participante.setToken(result.getString("token"));
        participante.setProponente(result.getBoolean("proponente"));
        return participante;
    }
}
