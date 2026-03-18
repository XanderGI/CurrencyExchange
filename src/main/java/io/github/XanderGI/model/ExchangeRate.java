package io.github.XanderGI.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

@JsonPropertyOrder({"id", "baseCurrency", "targetCurrency", "rate"})
public class ExchangeRate {
    private final Long id;
    private final Currency baseCurrency;
    private final Currency targetCurrency;
    private BigDecimal rate;

    public ExchangeRate(Long id, Currency baseCurrency, Currency targetCurrency, BigDecimal rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getTargetCurrency() {
        return targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}