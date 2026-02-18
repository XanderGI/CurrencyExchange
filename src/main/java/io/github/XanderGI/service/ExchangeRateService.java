package io.github.XanderGI.service;

import io.github.XanderGI.dao.CurrencyDao;
import io.github.XanderGI.dao.ExchangeRateDao;
import io.github.XanderGI.dto.ExchangeRateRequestConvertDto;
import io.github.XanderGI.dto.ExchangeRateRequestDto;
import io.github.XanderGI.dto.ExchangeRateResponseConvertDto;
import io.github.XanderGI.exception.CurrencyNotFoundException;
import io.github.XanderGI.exception.ExchangeRateAlreadyExistsException;
import io.github.XanderGI.exception.ExchangeRateNotFoundException;
import io.github.XanderGI.mapper.ExchangeRateMapper;
import io.github.XanderGI.model.Currency;
import io.github.XanderGI.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {
    private final ExchangeRateDao exchangeRateDao;
    private final CurrencyDao currencyDao = new CurrencyDao();

    public ExchangeRateService(ExchangeRateDao dao) {
        this.exchangeRateDao = dao;
    }

    public ExchangeRateService() {
        this(new ExchangeRateDao());
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
        validate(dto);

        ExchangeRate exchangeRate = getExchangeRateByCode(
                dto.getBaseCurrencyCode(),
                dto.getTargetCurrencyCode()
        );
        exchangeRate.setRate(dto.getRate());

        return exchangeRateDao.update(exchangeRate)
                .orElseThrow(() -> new ExchangeRateNotFoundException("ExchangeRate not found"));
    }

    public ExchangeRateResponseConvertDto convertCurrency(ExchangeRateRequestConvertDto dto) {
        validate(dto);

        List<ExchangeRate> exchangeRates = exchangeRateDao.findAllUsdRelatedPairs(
                dto.getBaseCurrencyCode(),
                dto.getTargetCurrencyCode()
        );

        String currencyCodeFrom = dto.getBaseCurrencyCode();
        String currencyCodeTo = dto.getTargetCurrencyCode();
        BigDecimal amount = dto.getAmount();


        return convertDirect(currencyCodeFrom, currencyCodeTo, amount, exchangeRates)
                .or(() -> convertReverse(currencyCodeFrom, currencyCodeTo, amount, exchangeRates))
                .or(() -> convertCross(currencyCodeFrom, currencyCodeTo, amount, exchangeRates))
                .orElseThrow(() -> new ExchangeRateNotFoundException(
                        "ExchangeRate for pair " + currencyCodeFrom + "-" + currencyCodeTo + " not found"
                ));
    }

    private Optional<ExchangeRateResponseConvertDto> convertDirect(String base, String target, BigDecimal amount, List<ExchangeRate> exchangeRates) {
        return exchangeRates.stream()
                .filter(exRate -> isPair(exRate, base, target))
                .findFirst()
                .map(exRate -> {
                    BigDecimal rate = exRate.getRate().setScale(6, RoundingMode.HALF_EVEN);
                    BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);

                    return ExchangeRateMapper.toResponseDto(
                            exRate.getBaseCurrency(),
                            exRate.getTargetCurrency(),
                            rate,
                            amount,
                            convertedAmount
                    );
                });
    }

    private Optional<ExchangeRateResponseConvertDto> convertReverse(String base, String target, BigDecimal amount, List<ExchangeRate> exchangeRates) {
        return exchangeRates.stream()
                .filter(exRate -> isPair(exRate, target, base))
                .findFirst()
                .map(exRate -> {
                    BigDecimal rate = BigDecimal.ONE.divide(exRate.getRate(), 6, RoundingMode.HALF_EVEN);
                    BigDecimal convertedAmount = rate.multiply(amount).setScale(2, RoundingMode.HALF_EVEN);

                    return ExchangeRateMapper.toResponseDto(
                            exRate.getTargetCurrency(),
                            exRate.getBaseCurrency(),
                            rate,
                            amount,
                            convertedAmount
                    );
                });
    }

    private Optional<ExchangeRateResponseConvertDto> convertCross(String base, String target, BigDecimal amount, List<ExchangeRate> exchangeRates) {
        return findRateForCurrency(base, exchangeRates)
                .flatMap(usdToBaseRate -> findRateForCurrency(target, exchangeRates)
                        .map(usdToTargetRate -> {
                            BigDecimal rateBaseToUsd = calculateRate(usdToBaseRate, true);
                            BigDecimal rateUsdToTarget = calculateRate(usdToTargetRate, false);

                            Currency baseCurrency = extractNonUsdCurrency(usdToBaseRate);
                            Currency targetCurrency = extractNonUsdCurrency(usdToTargetRate);
                            BigDecimal rate = rateBaseToUsd.multiply(rateUsdToTarget).setScale(6, RoundingMode.HALF_EVEN);
                            BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);

                            return ExchangeRateMapper.toResponseDto(
                                    baseCurrency,
                                    targetCurrency,
                                    rate,
                                    amount,
                                    convertedAmount
                            );
                        }));
    }

    private void validate(ExchangeRateRequestDto dto) {
        checkCurrencyCodesAreDifferent(dto.getBaseCurrencyCode(), dto.getTargetCurrencyCode());

        if (dto.getRate().signum() <= 0) {
            throw new IllegalArgumentException("The rate value must be positive");
        }
    }

    private void validate(ExchangeRateRequestConvertDto dto) {
        checkCurrencyCodesAreDifferent(dto.getBaseCurrencyCode(), dto.getTargetCurrencyCode());

        if (dto.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("The amount value must be positive");
        }
    }

    private void checkCurrencyCodesAreDifferent(String base, String target) {
        if (base.equals(target)) {
            throw new IllegalArgumentException("Currency codes should not be repeated");
        }
    }

    private boolean isPair(ExchangeRate exchangeRate, String baseCode, String targetCode) {
        return exchangeRate.getBaseCurrency().getCode().equals(baseCode) &&
                exchangeRate.getTargetCurrency().getCode().equals(targetCode);
    }

    private Optional<ExchangeRate> findRateForCurrency(String code, List<ExchangeRate> exchangeRates) {
        return exchangeRates.stream()
                .filter(exRate -> containsCurrency(exRate, code))
                .findFirst();
    }

    private boolean containsCurrency(ExchangeRate exchangeRate, String code) {
        return exchangeRate.getBaseCurrency().getCode().equals(code) ||
                exchangeRate.getTargetCurrency().getCode().equals(code);
    }

    private BigDecimal calculateRate(ExchangeRate exchangeRate, boolean toUsd) {
        boolean isBaseUsd = exchangeRate.getBaseCurrency().getCode().equals("USD");
        if (isBaseUsd == toUsd) {
            return BigDecimal.ONE.divide(exchangeRate.getRate(), 6, RoundingMode.HALF_UP);
        }
        return exchangeRate.getRate();
    }

    private Currency extractNonUsdCurrency(ExchangeRate exchangeRate) {
        if (exchangeRate.getBaseCurrency().getCode().equals("USD")) {
            return exchangeRate.getTargetCurrency();
        }
        return exchangeRate.getBaseCurrency();
    }
}