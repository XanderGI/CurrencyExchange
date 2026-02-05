package io.github.XanderGI.service;

import io.github.XanderGI.dao.CurrencyDao;
import io.github.XanderGI.exception.CurrencyNotFoundException;
import io.github.XanderGI.model.Currency;

import java.util.List;

public class CurrencyService {
    private final CurrencyDao currencyDao = new CurrencyDao();

    public List<Currency> getAllCurrencies() {
        return currencyDao.findAll();
    }

    public Currency getCurrencyByCode(String code) {
        return currencyDao.findByCode(code)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found by code: " + code));
    }
}