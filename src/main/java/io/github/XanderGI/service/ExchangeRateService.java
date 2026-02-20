package io.github.XanderGI.service;

import io.github.XanderGI.dao.CurrencyDao;
import io.github.XanderGI.dao.ExchangeRateDao;
import io.github.XanderGI.dto.ExchangeRateRequestDto;
import io.github.XanderGI.exception.CurrencyNotFoundException;
import io.github.XanderGI.exception.ExchangeRateAlreadyExistsException;
import io.github.XanderGI.exception.ExchangeRateNotFoundException;
import io.github.XanderGI.model.Currency;
import io.github.XanderGI.model.ExchangeRate;

import java.util.List;

import static io.github.XanderGI.utils.ValidationUtils.validate;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao;
    private final CurrencyDao currencyDao;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao, CurrencyDao currencyDao) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyDao = currencyDao;
    }

    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRateDao.findAll();
    }

    public ExchangeRate getExchangeRateByCode(String baseCode, String targetCode) {
        return exchangeRateDao.findByCodes(baseCode, targetCode)
                .orElseThrow(() -> new ExchangeRateNotFoundException("ExchangeRate not found"));
    }

    public ExchangeRate addExchangeRate(ExchangeRateRequestDto dto) {
        validate(dto);

        Currency baseCurrency = currencyDao.findByCode(dto.baseCurrencyCode())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found by code"));
        Currency targetCurrency = currencyDao.findByCode(dto.targetCurrencyCode())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found by code"));

        ExchangeRate exchangeRate = new ExchangeRate(
                baseCurrency,
                targetCurrency,
                dto.rate()
        );

        return exchangeRateDao.save(exchangeRate)
                .orElseThrow(() -> new ExchangeRateAlreadyExistsException("ExchangeRate already exists"));
    }

    public ExchangeRate updateExchangeRate(ExchangeRateRequestDto dto) {
        validate(dto);

        ExchangeRate exchangeRate = getExchangeRateByCode(
                dto.baseCurrencyCode(),
                dto.targetCurrencyCode()
        );
        exchangeRate.setRate(dto.rate());

        return exchangeRateDao.update(exchangeRate)
                .orElseThrow(() -> new ExchangeRateNotFoundException("ExchangeRate not found"));
    }
}