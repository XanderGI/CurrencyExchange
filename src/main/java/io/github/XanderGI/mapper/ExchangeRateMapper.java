package io.github.XanderGI.mapper;

import io.github.XanderGI.dto.ExchangeRateRequestDto;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public class ExchangeRateMapper {

    public static ExchangeRateRequestDto toDto(HttpServletRequest req) {
        return new ExchangeRateRequestDto(
                req.getParameter("baseCurrencyCode"),
                req.getParameter("targetCurrencyCode"),
                new BigDecimal(req.getParameter("rate"))
        );
    }

    public static ExchangeRateRequestDto toDtoFromPatchRequest(String baseCode, String targetCode, String rate) {
        return new ExchangeRateRequestDto(
                baseCode,
                targetCode,
                new BigDecimal(rate)
        );
    }
}