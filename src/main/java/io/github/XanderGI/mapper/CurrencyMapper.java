package io.github.XanderGI.mapper;

import io.github.XanderGI.dto.CurrencyRequestDto;
import io.github.XanderGI.model.Currency;
import jakarta.servlet.http.HttpServletRequest;

public class CurrencyMapper {
    public static CurrencyRequestDto toDto(HttpServletRequest req) {
        return new CurrencyRequestDto(
                req.getParameter("name"),
                req.getParameter("code"),
                req.getParameter("sign")
        );
    }

    public static Currency toModel(CurrencyRequestDto currencyRequestDto) {
        return new Currency(
                currencyRequestDto.getName(),
                currencyRequestDto.getCode(),
                currencyRequestDto.getSign()
        );
    }
}