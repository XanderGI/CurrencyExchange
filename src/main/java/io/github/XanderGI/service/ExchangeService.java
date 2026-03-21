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
    private static final String CROSS_CONVERT_CURRENCY = "USD";
    private static final int RATE_SCALE = 6;
    private static final int AMOUNT_SCALE = 2;
    private final ExchangeRateDao exchangeRateDao;
    private final ExchangeRateMapper mapper;

    private record ConversionResult(Currency base, Currency target, BigDecimal rate) {
    }

    public ExchangeService(ExchangeRateDao exchangeRateDao, ExchangeRateMapper exchangeRateMapper) {
        this.exchangeRateDao = exchangeRateDao;
        this.mapper = exchangeRateMapper;
    }

    public ExchangeRateResponseConvertDto convertCurrency(ExchangeRateRequestConvertDto dto) {
        validate(dto);

        String currencyCodeFrom = dto.baseCurrencyCode();
        String currencyCodeTo = dto.targetCurrencyCode();
        BigDecimal amount = dto.amount();

        ConversionResult conversionResult = convertDirect(currencyCodeFrom, currencyCodeTo)
                .or(() -> convertReverse(currencyCodeFrom, currencyCodeTo))
                .or(() -> convertCross(currencyCodeFrom, currencyCodeTo))
                .orElseThrow(() -> new ExchangeRateNotFoundException(
                        "ExchangeRate for pair " + currencyCodeFrom + "-" + currencyCodeTo + " not found"
                ));

        BigDecimal convertedAmount = amount.multiply(conversionResult.rate()).setScale(AMOUNT_SCALE, RoundingMode.HALF_EVEN);

        return mapper.toResponseDto(
                conversionResult.base(),
                conversionResult.target(),
                conversionResult.rate(),
                amount,
                convertedAmount
        );
    }

    private Optional<ConversionResult> convertDirect(String base, String target) {
        return exchangeRateDao.findByCodes(base, target)
                .map(exRate -> {
                    BigDecimal rate = exRate.getRate().setScale(RATE_SCALE, RoundingMode.HALF_EVEN);
                    return new ConversionResult(exRate.getBaseCurrency(), exRate.getTargetCurrency(), rate);
                });
    }

    private Optional<ConversionResult> convertReverse(String base, String target) {
        return exchangeRateDao.findByCodes(target, base)
                .map(exRate -> {
                    BigDecimal rate = BigDecimal.ONE.divide(exRate.getRate(), RATE_SCALE, RoundingMode.HALF_EVEN);
                    return new ConversionResult(exRate.getTargetCurrency(), exRate.getBaseCurrency(), rate);
                });
    }

    private Optional<ConversionResult> convertCross(String base, String target) {
        List<ExchangeRate> exchangeRates = exchangeRateDao.findAllUsdRelatedPairs(base, target);
        Optional<ExchangeRate> usdToBaseOpt = findRateForCurrency(base, exchangeRates);
        Optional<ExchangeRate> usdToTargetOpt = findRateForCurrency(target, exchangeRates);

        if (usdToBaseOpt.isEmpty() || usdToTargetOpt.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate usdToBaseRate = usdToBaseOpt.get();
        ExchangeRate usdToTargetRate = usdToTargetOpt.get();

        BigDecimal rateBaseToUsd = calculateRateToUsd(usdToBaseRate);
        BigDecimal rateUsdToTarget = calculateRateFromUsd(usdToTargetRate);
        Currency baseCurrency = extractNonUsdCurrency(usdToBaseRate);
        Currency targetCurrency = extractNonUsdCurrency(usdToTargetRate);
        BigDecimal rate = rateBaseToUsd.multiply(rateUsdToTarget).setScale(RATE_SCALE, RoundingMode.HALF_EVEN);

        ConversionResult result = new ConversionResult(baseCurrency, targetCurrency, rate);

        return Optional.of(result);
    }

    private BigDecimal calculateRateToUsd(ExchangeRate exchangeRate) {
        boolean isUsdBase = exchangeRate.getBaseCurrency().code().equals(CROSS_CONVERT_CURRENCY);

        if (isUsdBase) {
            return BigDecimal.ONE.divide(exchangeRate.getRate(), RATE_SCALE, RoundingMode.HALF_EVEN);
        }

        return exchangeRate.getRate();
    }

    private BigDecimal calculateRateFromUsd(ExchangeRate exchangeRate) {
        boolean isUsdBase = exchangeRate.getBaseCurrency().code().equals(CROSS_CONVERT_CURRENCY);

        if (isUsdBase) {
            return exchangeRate.getRate();
        }

        return BigDecimal.ONE.divide(exchangeRate.getRate(), RATE_SCALE, RoundingMode.HALF_EVEN);
    }

    private Currency extractNonUsdCurrency(ExchangeRate exchangeRate) {
        if (exchangeRate.getBaseCurrency().code().equals(CROSS_CONVERT_CURRENCY)) {
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