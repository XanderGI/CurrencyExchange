package io.github.XanderGI.listener;

import io.github.XanderGI.dao.CurrencyDao;
import io.github.XanderGI.dao.ExchangeRateDao;
import io.github.XanderGI.dao.impl.CurrencyDaoImpl;
import io.github.XanderGI.dao.impl.ExchangeRateDaoImpl;
import io.github.XanderGI.service.CurrencyService;
import io.github.XanderGI.service.ExchangeRateService;
import io.github.XanderGI.service.ExchangeService;
import io.github.XanderGI.utils.DatabaseManager;
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
        DatabaseManager.init();

        CurrencyDao currencyDao = new CurrencyDaoImpl();
        ExchangeRateDao exchangeRateDao = new ExchangeRateDaoImpl();

        CurrencyService currencyService = new CurrencyService(currencyDao);
        ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateDao, currencyDao);
        ExchangeService exchangeService = new ExchangeService(exchangeRateDao);

        sce.getServletContext().setAttribute("currencyService", currencyService);
        sce.getServletContext().setAttribute("exchangeRateService", exchangeRateService);
        sce.getServletContext().setAttribute("exchangeService", exchangeService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseManager.close();
    }
}