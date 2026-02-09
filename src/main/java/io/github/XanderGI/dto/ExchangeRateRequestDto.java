package io.github.XanderGI.dto;

import java.math.BigDecimal;

public class ExchangeRateRequestDto {
    private final String baseCurrencyCode;
    private final String targetCurrencyCode;
    private final BigDecimal rate;

    public ExchangeRateRequestDto(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
        this.baseCurrencyCode = baseCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
        this.rate = rate;
    }

    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    public BigDecimal getRate() {
        return rate;
    }
}