package io.github.XanderGI.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseManager {
    private static final String POOL_SIZE_DEFAULT = "20";
    private static HikariDataSource dataSource;

    private DatabaseManager() {

    }

    public static void init() {
        if (dataSource != null) {
            return;
        }

        HikariConfig config = new HikariConfig();

        try (InputStream stream = DatabaseManager.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (stream == null) {
                throw new RuntimeException("properties file not found!");
            }

            Properties properties = new Properties();
            properties.load(stream);

            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setDriverClassName(properties.getProperty("db.driver"));
            String poolSize = properties.getProperty("db.pool.size", POOL_SIZE_DEFAULT);
            config.setMaximumPoolSize(Integer.parseInt(poolSize));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database application.properties", e);
        }

        String envUrl = System.getenv("DB_URL");
        if (envUrl != null) {
            config.setJdbcUrl(envUrl);
        }

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

    public static DataSource getDataSource() {
        return dataSource;
    }
}