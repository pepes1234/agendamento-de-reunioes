package br.edu.agendamento.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("Driver MySQL nao encontrado.", exception);
        }
        return DriverManager.getConnection(
                Environment.get("DB_URL",
                        "jdbc:mysql://localhost:3306/sistema_reunioes?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"),
                Environment.get("DB_USER"),
                Environment.get("DB_PASSWORD"));
    }
}
