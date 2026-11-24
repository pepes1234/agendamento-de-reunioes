package br.edu.agendamento.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.edu.agendamento.config.DatabaseConnection;
import br.edu.agendamento.model.Horario;
import br.edu.agendamento.model.Reuniao;

public class ReuniaoRepository {
    private static final String COLUNAS =
            "id, titulo, descricao, local_reuniao, nome_proponente, email_proponente, "
            + "token_publico, token_admin, data_inicio, data_fim, modo_divisao, "
            + "hora_inicio, hora_fim, duracao_minutos";

    public Reuniao inserir(Reuniao reuniao) throws SQLException {
        String sql = "INSERT INTO reuniao "
                + "(titulo, descricao, local_reuniao, nome_proponente, email_proponente, token_publico, token_admin, "
                + "data_inicio, data_fim, modo_divisao, hora_inicio, hora_fim, duracao_minutos) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, reuniao.getTitulo());
            statement.setString(2, reuniao.getDescricao());
            statement.setString(3, reuniao.getLocalReuniao());
            statement.setString(4, reuniao.getNomeProponente());
            statement.setString(5, reuniao.getEmailProponente());
            statement.setString(6, reuniao.getTokenPublico());
            statement.setString(7, reuniao.getTokenAdmin());
            statement.setDate(8, Date.valueOf(reuniao.getDataInicio()));
            statement.setDate(9, Date.valueOf(reuniao.getDataFim()));
            statement.setString(10, reuniao.getModoDivisao());
            statement.setTime(11, reuniao.getHoraInicio() == null ? null : Time.valueOf(reuniao.getHoraInicio()));
            statement.setTime(12, reuniao.getHoraFim() == null ? null : Time.valueOf(reuniao.getHoraFim()));
            if (reuniao.getDuracaoMinutos() == null) statement.setObject(13, null);
            else statement.setInt(13, reuniao.getDuracaoMinutos());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) reuniao.setId(keys.getLong(1));
            }
            return reuniao;
        }
    }

    public Reuniao buscarPorId(long id) throws SQLException {
        return buscarPor("id", id);
    }

    public Reuniao buscarPorTokenAdmin(String token) throws SQLException {
        return buscarPor("token_admin", token);
    }

    public Reuniao buscarPorTokenPublico(String token) throws SQLException {
        return buscarPor("token_publico", token);
    }

    private Reuniao buscarPor(String coluna, Object valor) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM reuniao WHERE " + coluna + " = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, valor);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapearReuniao(result) : null;
            }
        }
    }

    public List<Reuniao> listarCriadasPor(String email) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM reuniao "
                + "WHERE LOWER(email_proponente) = LOWER(?) ORDER BY criada_em DESC";
        return listarPorEmail(sql, email);
    }

    public List<Reuniao> listarParticipadasPor(String email) throws SQLException {
        String sql = "SELECT r." + COLUNAS.replace(", ", ", r.")
                + " FROM reuniao r "
                + "WHERE EXISTS (SELECT 1 FROM participante p "
                + "WHERE p.reuniao_id = r.id AND LOWER(p.email) = LOWER(?)) "
                + "AND LOWER(r.email_proponente) <> LOWER(?) "
                + "ORDER BY r.criada_em DESC";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, email);
            List<Reuniao> reunioes = new ArrayList<>();
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) reunioes.add(mapearReuniao(result));
            }
            return reunioes;
        }
    }

    private List<Reuniao> listarPorEmail(String sql, String email) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            List<Reuniao> reunioes = new ArrayList<>();
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) reunioes.add(mapearReuniao(result));
            }
            return reunioes;
        }
    }

    public void excluir(long id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM reuniao WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public void inserirHorarios(long reuniaoId, List<Horario> horarios) throws SQLException {
        String sql = "INSERT INTO horario (reuniao_id, dia, periodo, hora_inicio, hora_fim) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Horario horario : horarios) {
                statement.setLong(1, reuniaoId);
                statement.setDate(2, Date.valueOf(horario.getDia()));
                statement.setString(3, horario.getPeriodo());
                statement.setTime(4, horario.getHoraInicio() == null ? null : Time.valueOf(horario.getHoraInicio()));
                statement.setTime(5, horario.getHoraFim() == null ? null : Time.valueOf(horario.getHoraFim()));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public List<Horario> listarHorarios(long reuniaoId) throws SQLException {
        String sql = "SELECT id, reuniao_id, dia, periodo, hora_inicio, hora_fim "
                + "FROM horario WHERE reuniao_id = ? "
                + "ORDER BY dia, hora_inicio, periodo";
        List<Horario> horarios = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reuniaoId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    Horario horario = new Horario();
                    horario.setId(result.getLong("id"));
                    horario.setReuniaoId(result.getLong("reuniao_id"));
                    horario.setDia(result.getObject("dia", LocalDate.class));
                    horario.setPeriodo(result.getString("periodo"));
                    Time inicio = result.getTime("hora_inicio");
                    Time fim = result.getTime("hora_fim");
                    horario.setHoraInicio(inicio == null ? null : inicio.toLocalTime());
                    horario.setHoraFim(fim == null ? null : fim.toLocalTime());
                    horarios.add(horario);
                }
            }
        }
        return horarios;
    }

    private Reuniao mapearReuniao(ResultSet result) throws SQLException {
        Reuniao reuniao = new Reuniao();
        reuniao.setId(result.getLong("id"));
        reuniao.setTitulo(result.getString("titulo"));
        reuniao.setDescricao(result.getString("descricao"));
        reuniao.setLocalReuniao(result.getString("local_reuniao"));
        reuniao.setNomeProponente(result.getString("nome_proponente"));
        reuniao.setEmailProponente(result.getString("email_proponente"));
        reuniao.setTokenPublico(result.getString("token_publico"));
        reuniao.setTokenAdmin(result.getString("token_admin"));
        reuniao.setDataInicio(result.getObject("data_inicio", LocalDate.class));
        reuniao.setDataFim(result.getObject("data_fim", LocalDate.class));
        reuniao.setModoDivisao(result.getString("modo_divisao"));
        Time inicio = result.getTime("hora_inicio");
        Time fim = result.getTime("hora_fim");
        reuniao.setHoraInicio(inicio == null ? null : inicio.toLocalTime());
        reuniao.setHoraFim(fim == null ? null : fim.toLocalTime());
        int duracao = result.getInt("duracao_minutos");
        reuniao.setDuracaoMinutos(result.wasNull() ? null : duracao);
        return reuniao;
    }
}
