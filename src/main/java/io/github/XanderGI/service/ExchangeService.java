package io.github.XanderGI.service;

import io.github.XanderGI.dao.ExchangeRateDao;
import io.github.XanderGI.dto.ExchangeRateRequestConvertDto;
import io.github.XanderGI.dto.ExchangeRateResponseConvertDto;
import io.github.XanderGI.exception.ExchangeRateNotFoundException;
import io.github.XanderGI.mapper.ExchangeRateMapper;
import io.github.XanderGI.model.Currency;
import io.github.XanderGI.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static io.github.XanderGI.utils.ValidationUtils.validate;

public class ExchangeService {
    private final ExchangeRateDao exchangeRateDao;
    private final ExchangeRateMapper mapper;

    public ExchangeService(ExchangeRateDao exchangeRateDao, ExchangeRateMapper exchangeRateMapper) {
        this.exchangeRateDao = exchangeRateDao;
        this.mapper = exchangeRateMapper;
    }

    public ExchangeRateResponseConvertDto convertCurrency(ExchangeRateRequestConvertDto dto) {
        validate(dto);

        String currencyCodeFrom = dto.baseCurrencyCode();
        String currencyCodeTo = dto.targetCurrencyCode();
        BigDecimal amount = dto.amount();


        return convertDirect(currencyCodeFrom, currencyCodeTo, amount)
                .or(() -> convertReverse(currencyCodeFrom, currencyCodeTo, amount))
                .or(() -> convertCross(currencyCodeFrom, currencyCodeTo, amount))
                .orElseThrow(() -> new ExchangeRateNotFoundException(
                        "ExchangeRate for pair " + currencyCodeFrom + "-" + currencyCodeTo + " not found"
                ));
    }

    private Optional<ExchangeRateResponseConvertDto> convertDirect(String base, String target, BigDecimal amount) {
        return exchangeRateDao.findByCodes(base, target)
                .map(exRate -> {
                    BigDecimal rate = exRate.getRate().setScale(6, RoundingMode.HALF_EVEN);
                    BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);

                    return mapper.toResponseDto(
                            exRate.getBaseCurrency(),
                            exRate.getTargetCurrency(),
                            rate,
                            amount,
                            convertedAmount
                    );
                });
    }

    private Optional<ExchangeRateResponseConvertDto> convertReverse(String base, String target, BigDecimal amount) {
        return exchangeRateDao.findByCodes(target, base)
                .map(exRate -> {
                    BigDecimal rate = BigDecimal.ONE.divide(exRate.getRate(), 6, RoundingMode.HALF_EVEN);
                    BigDecimal convertedAmount = rate.multiply(amount).setScale(2, RoundingMode.HALF_EVEN);

                    return mapper.toResponseDto(
                            exRate.getTargetCurrency(),
                            exRate.getBaseCurrency(),
                            rate,
                            amount,
                            convertedAmount
                    );
                });
    }

    private Optional<ExchangeRateResponseConvertDto> convertCross(String base, String target, BigDecimal amount) {
        List<ExchangeRate> exchangeRates = exchangeRateDao.findAllUsdRelatedPairs(base, target);

        return findRateForCurrency(base, exchangeRates)
                .flatMap(usdToBaseRate -> findRateForCurrency(target, exchangeRates)
                        .map(usdToTargetRate -> {
                            BigDecimal rateBaseToUsd = calculateRate(usdToBaseRate, true);
                            BigDecimal rateUsdToTarget = calculateRate(usdToTargetRate, false);

                            Currency baseCurrency = extractNonUsdCurrency(usdToBaseRate);
                            Currency targetCurrency = extractNonUsdCurrency(usdToTargetRate);
                            BigDecimal rate = rateBaseToUsd.multiply(rateUsdToTarget).setScale(6, RoundingMode.HALF_EVEN);
                            BigDecimal convertedAmount = amount.multiply(rate).setScale(2, RoundingMode.HALF_EVEN);

                            return mapper.toResponseDto(
                                    baseCurrency,
                                    targetCurrency,
                                    rate,
                                    amount,
                                    convertedAmount
                            );
                        }));
    }

    private BigDecimal calculateRate(ExchangeRate exchangeRate, boolean toUsd) {
        boolean isBaseUsd = exchangeRate.getBaseCurrency().code().equals("USD");
        if (isBaseUsd == toUsd) {
            return BigDecimal.ONE.divide(exchangeRate.getRate(), 6, RoundingMode.HALF_UP);
        }
        return exchangeRate.getRate();
    }

    private Currency extractNonUsdCurrency(ExchangeRate exchangeRate) {
        if (exchangeRate.getBaseCurrency().code().equals("USD")) {
            return exchangeRate.getTargetCurrency();
        }
        return exchangeRate.getBaseCurrency();
    }

    private Optional<ExchangeRate> findRateForCurrency(String code, List<ExchangeRate> exchangeRates) {
        return exchangeRates.stream()
                .filter(exRate -> containsCurrency(exRate, code))
                .findFirst();
    }

    private boolean containsCurrency(ExchangeRate exchangeRate, String code) {
        return exchangeRate.getBaseCurrency().code().equals(code) ||
                exchangeRate.getTargetCurrency().code().equals(code);
    }
}