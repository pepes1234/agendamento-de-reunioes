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
import br.edu.agendamento.model.OpcaoAgendamento;
import br.edu.agendamento.model.Reuniao;

public class ReuniaoRepository {
    public Reuniao inserir(Reuniao reuniao) throws SQLException {
        String sql = "INSERT INTO reuniao "
                + "(nome, data_inicio, data_fim, formato, hora_inicio, hora_fim, intervalo_minutos, token_exclusao) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, reuniao.getNome());
            statement.setDate(2, Date.valueOf(reuniao.getDataInicio()));
            statement.setDate(3, Date.valueOf(reuniao.getDataFim()));
            statement.setString(4, reuniao.getFormato());
            statement.setTime(5, reuniao.getHoraInicio() == null ? null : Time.valueOf(reuniao.getHoraInicio()));
            statement.setTime(6, reuniao.getHoraFim() == null ? null : Time.valueOf(reuniao.getHoraFim()));
            statement.setInt(7, reuniao.getIntervaloMinutos());
            statement.setString(8, reuniao.getTokenExclusao());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    reuniao.setId(keys.getLong(1));
                }
            }
            return reuniao;
        }
    }

    public List<Reuniao> listarTodas() throws SQLException {
        String sql = "SELECT id, nome, data_inicio, data_fim, formato, hora_inicio, hora_fim, "
                + "intervalo_minutos, token_exclusao FROM reuniao ORDER BY criado_em DESC";
        List<Reuniao> reunioes = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                reunioes.add(mapearReuniao(result));
            }
        }
        return reunioes;
    }

    public Reuniao buscarPorId(long id) throws SQLException {
        String sql = "SELECT id, nome, data_inicio, data_fim, formato, hora_inicio, hora_fim, "
                + "intervalo_minutos, token_exclusao FROM reuniao WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapearReuniao(result) : null;
            }
        }
    }

    public Reuniao buscarPorTokenExclusao(String token) throws SQLException {
        String sql = "SELECT id, nome, data_inicio, data_fim, formato, hora_inicio, hora_fim, "
                + "intervalo_minutos, token_exclusao FROM reuniao WHERE token_exclusao = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, token);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? mapearReuniao(result) : null;
            }
        }
    }

    public void excluir(long id) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM reuniao WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public void inserirOpcoes(long reuniaoId, List<OpcaoAgendamento> opcoes) throws SQLException {
        String sql = "INSERT INTO opcao_agendamento "
                + "(reuniao_id, data_opcao, turno, hora_inicio, hora_fim) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            for (OpcaoAgendamento opcao : opcoes) {
                statement.setLong(1, reuniaoId);
                statement.setDate(2, Date.valueOf(opcao.getDataOpcao()));
                statement.setString(3, opcao.getTurno());
                if (opcao.getHoraInicio() == null) {
                    statement.setObject(4, null);
                    statement.setObject(5, null);
                } else {
                    statement.setTime(4, Time.valueOf(opcao.getHoraInicio()));
                    statement.setTime(5, Time.valueOf(opcao.getHoraFim()));
                }
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    public List<OpcaoAgendamento> listarOpcoes(long reuniaoId) throws SQLException {
        String sql = "SELECT id, reuniao_id, data_opcao, turno, hora_inicio, hora_fim "
                + "FROM opcao_agendamento WHERE reuniao_id = ? "
                + "ORDER BY data_opcao, hora_inicio, turno";
        List<OpcaoAgendamento> opcoes = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, reuniaoId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    OpcaoAgendamento opcao = new OpcaoAgendamento();
                    opcao.setId(result.getLong("id"));
                    opcao.setReuniaoId(result.getLong("reuniao_id"));
                    opcao.setDataOpcao(result.getObject("data_opcao", LocalDate.class));
                    opcao.setTurno(result.getString("turno"));
                    Time inicio = result.getTime("hora_inicio");
                    Time fim = result.getTime("hora_fim");
                    opcao.setHoraInicio(inicio == null ? null : inicio.toLocalTime());
                    opcao.setHoraFim(fim == null ? null : fim.toLocalTime());
                    opcoes.add(opcao);
                }
            }
        }
        return opcoes;
    }

    private Reuniao mapearReuniao(ResultSet result) throws SQLException {
        Reuniao reuniao = new Reuniao();
        reuniao.setId(result.getLong("id"));
        reuniao.setNome(result.getString("nome"));
        reuniao.setDataInicio(result.getObject("data_inicio", LocalDate.class));
        reuniao.setDataFim(result.getObject("data_fim", LocalDate.class));
        reuniao.setFormato(result.getString("formato"));
        Time inicio = result.getTime("hora_inicio");
        Time fim = result.getTime("hora_fim");
        reuniao.setHoraInicio(inicio == null ? null : inicio.toLocalTime());
        reuniao.setHoraFim(fim == null ? null : fim.toLocalTime());
        reuniao.setIntervaloMinutos(result.getInt("intervalo_minutos"));
        reuniao.setTokenExclusao(result.getString("token_exclusao"));
        return reuniao;
    }
}
