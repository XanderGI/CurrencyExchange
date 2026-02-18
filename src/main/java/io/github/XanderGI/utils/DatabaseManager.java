package io.github.XanderGI.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseManager {
    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource dataSource;

    private DatabaseManager() {

    }

    public static void init() {
        config.setJdbcUrl("jdbc:sqlite:currencyExchange.db");
        config.setMaximumPoolSize(20);
        dataSource = new HikariDataSource(config);
    }

    public static void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}