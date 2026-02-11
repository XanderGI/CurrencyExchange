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

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private final CurrencyDao currencyDao = new CurrencyDao();

    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRateDao.findAll();
    }

    public ExchangeRate getExchangeRateByCode(String baseCode, String targetCode) {
        return exchangeRateDao.findByCodes(baseCode, targetCode)
                .orElseThrow(() -> new ExchangeRateNotFoundException("ExchangeRate not found"));
    }

    public ExchangeRate addExchangeRate(ExchangeRateRequestDto dto) {
        validateExchangeRateData(dto);

        Currency baseCurrency = currencyDao.findByCode(dto.getBaseCurrencyCode())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found by code"));
        Currency targetCurrency = currencyDao.findByCode(dto.getTargetCurrencyCode())
                .orElseThrow(() -> new CurrencyNotFoundException("Currency not found by code"));

        ExchangeRate exchangeRate = new ExchangeRate(
                baseCurrency,
                targetCurrency,
                dto.getRate()
        );

        return exchangeRateDao.save(exchangeRate)
                .orElseThrow(() -> new ExchangeRateAlreadyExistsException("ExchangeRate already exist"));
    }

    public ExchangeRate updateExchangeRate(ExchangeRateRequestDto dto) {
        validateExchangeRateData(dto);

        ExchangeRate exchangeRate = getExchangeRateByCode(
                dto.getBaseCurrencyCode(),
                dto.getTargetCurrencyCode()
        );
        exchangeRate.setRate(dto.getRate());

        return exchangeRateDao.update(exchangeRate)
                .orElseThrow(() -> new ExchangeRateNotFoundException("ExchangeRate not found"));
    }

    private void validateExchangeRateData(ExchangeRateRequestDto dto) {
        if (dto.getRate().signum() <= 0) {
            throw new IllegalArgumentException("The rate value must be positive");
        }

        if (dto.getBaseCurrencyCode().equals(dto.getTargetCurrencyCode())) {
            throw new IllegalArgumentException("Currency codes should not be repeated");
        }
    }
}