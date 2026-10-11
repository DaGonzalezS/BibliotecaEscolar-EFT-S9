package config;

import interfaces.SqlOperation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Properties;

public final class DatabaseConnection {
    private static final DatabaseConnection INSTANCE = new DatabaseConnection();
    private Connection connection;
    private final Properties properties = new Properties();

    private DatabaseConnection() {
        properties.setProperty("url", "jdbc:mysql://localhost:3306/biblioteca");
        properties.setProperty("user", "root");
        properties.setProperty("password", "Filusa.2026");
        Path file = Path.of("database.properties");
        if (Files.exists(file)) {
            try (InputStream input = Files.newInputStream(file)) {
                properties.load(input);
            } catch (IOException e) {
                throw new IllegalStateException("No se pudo leer database.properties", e);
            }
        }
    }

    public static DatabaseConnection getInstance() {
        return INSTANCE;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection =
                    DriverManager.getConnection(
                            properties.getProperty("url"),
                            properties.getProperty("user"),
                            properties.getProperty("password"));
        }
        return connection;
    }

    public synchronized <T> T consultar(SqlOperation<T> operation) throws SQLException {
        return operation.ejecutar(getConnection());
    }

    public synchronized <T> T transaccion(SqlOperation<T> operation) throws SQLException {
        Connection conn = getConnection();
        boolean anterior = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            T result = operation.ejecutar(conn);
            conn.commit();
            return result;
        } catch (SQLException | RuntimeException e) {
            try {
                conn.rollback();
            } catch (SQLException rollback) {
                e.addSuppressed(rollback);
            }
            throw e;
        } finally {
            conn.setAutoCommit(anterior);
        }
    }
}
