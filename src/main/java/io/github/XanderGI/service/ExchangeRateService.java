package io.github.XanderGI.service;

import io.github.XanderGI.dao.ExchangeRateDao;
import io.github.XanderGI.model.ExchangeRate;

import java.util.List;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRateDao.findAll();
    }
}