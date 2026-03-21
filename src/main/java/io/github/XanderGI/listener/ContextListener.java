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
    public static final String CURRENCY_SERVICE_ATTRIBUTE = "currencyService";
    public static final String EXCHANGE_RATE_SERVICE_ATTRIBUTE = "exchangeRateService";
    public static final String EXCHANGE_SERVICE_ATTRIBUTE = "exchangeService";
    public static final String CURRENCY_MAPPER_ATTRIBUTE = "currencyMapper";
    public static final String EXCHANGE_RATE_MAPPER_ATTRIBUTE = "exchangeRateMapper";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DatabaseManager.init();
        Flyway.configure().dataSource(DatabaseManager.getDataSource()).load().migrate();

        CurrencyDao currencyDao = new CurrencyDaoImpl();
        ExchangeRateDao exchangeRateDao = new ExchangeRateDaoImpl();

        CurrencyMapper currencyMapper = CurrencyMapper.INSTANCE;
        ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.INSTANCE;

        CurrencyService currencyService = new CurrencyService(currencyDao, currencyMapper);
        ExchangeRateService exchangeRateService = new ExchangeRateService(exchangeRateDao, currencyService, exchangeRateMapper);
        ExchangeService exchangeService = new ExchangeService(exchangeRateDao, exchangeRateMapper);

        sce.getServletContext().setAttribute(CURRENCY_SERVICE_ATTRIBUTE, currencyService);
        sce.getServletContext().setAttribute(EXCHANGE_RATE_SERVICE_ATTRIBUTE, exchangeRateService);
        sce.getServletContext().setAttribute(EXCHANGE_SERVICE_ATTRIBUTE, exchangeService);
        sce.getServletContext().setAttribute(CURRENCY_MAPPER_ATTRIBUTE, currencyMapper);
        sce.getServletContext().setAttribute(EXCHANGE_RATE_MAPPER_ATTRIBUTE, exchangeRateMapper);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DatabaseManager.close();
    }
}