package io.github.XanderGI.listener;

import io.github.XanderGI.dao.CurrencyDao;
import io.github.XanderGI.dao.ExchangeRateDao;
import io.github.XanderGI.dao.impl.CurrencyDaoImpl;
import io.github.XanderGI.dao.impl.ExchangeRateDaoImpl;
import io.github.XanderGI.mapper.CurrencyMapper;
import io.github.XanderGI.mapper.ExchangeRateMapper;
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
        DatabaseManager.init();
        Flyway.configure().dataSource(DatabaseManager.getDataSource()).load().migrate();

        CurrencyDao currencyDao = new CurrencyDaoImpl();
        ExchangeRateDao exchangeRateDao = new ExchangeRateDaoImpl();

        CurrencyMapper currencyMapper = CurrencyMapper.INSTANCE;
        ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.INSTANCE;

        CurrencyService currencyService = new CurrencyService(currencyDao, currencyMapper);
        ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateDao, currencyDao, exchangeRateMapper);
        ExchangeService exchangeService = new ExchangeService(exchangeRateDao, exchangeRateMapper);

        sce.getServletContext().setAttribute("currencyService", currencyService);
        sce.getServletContext().setAttribute("exchangeRateService", exchangeRateService);
        sce.getServletContext().setAttribute("exchangeService", exchangeService);
        sce.getServletContext().setAttribute("currencyMapper", currencyMapper);
        sce.getServletContext().setAttribute("exchangeRateMapper", exchangeRateMapper);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseManager.close();
    }
}