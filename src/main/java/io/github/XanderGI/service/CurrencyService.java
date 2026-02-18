package io.github.XanderGI.service;

import io.github.XanderGI.dao.CurrencyDao;
import io.github.XanderGI.dto.CurrencyRequestDto;
import io.github.XanderGI.exception.CurrencyAlreadyExistsException;
import io.github.XanderGI.exception.CurrencyNotFoundException;
import io.github.XanderGI.mapper.CurrencyMapper;
import io.github.XanderGI.model.Currency;

import java.util.List;

public class CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    public List<Currency> getAllCurrencies() {
        return currencyDao.findAll();
    }

    public Currency getCurrencyByCode(String code) {
        return currencyDao.findByCode(code)
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found by code"));
    }

    public Currency addCurrency(CurrencyRequestDto dto) {
        Currency currency = CurrencyMapper.toModel(dto);
        return currencyDao.save(currency)
                .orElseThrow(() -> new CurrencyAlreadyExistsException("Currency already exists"));
    }
}