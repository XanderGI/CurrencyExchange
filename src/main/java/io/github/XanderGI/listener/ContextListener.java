package io.github.XanderGI.listeners;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.flywaydb.core.Flyway;

@WebListener
public class ContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        Flyway.configure().dataSource("jdbc:sqlite:currencyExchange.db", "","").load().migrate();
        System.out.println("Flyway migration completed successfully!");
    }
}