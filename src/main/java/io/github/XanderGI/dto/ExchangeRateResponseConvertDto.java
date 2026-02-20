package io.github.XanderGI.dto;

import io.github.XanderGI.model.Currency;

import java.math.BigDecimal;

public record ExchangeRateResponseConvertDto(
        Currency baseCurrency,
        Currency targetCurrency,
        BigDecimal rate,
        BigDecimal amount,
        BigDecimal convertedAmount) {
}