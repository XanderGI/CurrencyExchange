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
    private static final HikariConfig config = new HikariConfig();
    private static HikariDataSource dataSource;

    private DatabaseManager() {

    }

    public static void init() {
        try (InputStream stream = DatabaseManager.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (stream == null) {
                throw new RuntimeException("properties file not found!");
            }

            Properties properties = new Properties();
            properties.load(stream);

            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setDriverClassName(properties.getProperty("db.driver"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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

    public static DataSource getDataSource() {
        return dataSource;
    }
}