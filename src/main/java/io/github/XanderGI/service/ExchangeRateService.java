package io.github.XanderGI.service;

import io.github.XanderGI.dao.ExchangeRateDao;
import io.github.XanderGI.exception.ExchangeRateNotFoundException;
import io.github.XanderGI.model.ExchangeRate;

import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRateDao.findAll();
    }

    public ExchangeRate getExchangeRateByCode(String baseCode, String targetCode) {
        return exchangeRateDao.findByCodes(baseCode, targetCode)
                .orElseThrow(() -> new ExchangeRateNotFoundException("ExchangeRate not found"));
    }
}