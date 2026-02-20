package io.github.XanderGI.dto;

import java.math.BigDecimal;

public record ExchangeRateRequestConvertDto(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
}