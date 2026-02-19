package io.github.XanderGI.mapper;

import io.github.XanderGI.dto.ExchangeRateRequestConvertDto;
import io.github.XanderGI.dto.ExchangeRateRequestDto;
import io.github.XanderGI.dto.ExchangeRateResponseConvertDto;
import io.github.XanderGI.model.Currency;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public class ExchangeRateMapper {

    public static ExchangeRateRequestDto toRequestDto(String baseCode, String targetCode, String rate) {
        return new ExchangeRateRequestDto(
                baseCode,
                targetCode,
                new BigDecimal(rate)
        );
    }

    public static ExchangeRateRequestConvertDto toConvertDto(HttpServletRequest req) {
        return new ExchangeRateRequestConvertDto(
                req.getParameter("from"),
                req.getParameter("to"),
                new BigDecimal(req.getParameter("amount"))
        );
    }

    public static ExchangeRateResponseConvertDto toResponseDto(
            Currency baseCurrency, Currency targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        return new ExchangeRateResponseConvertDto(
                baseCurrency,
                targetCurrency,
                rate,
                amount,
                convertedAmount
        );
    }
}