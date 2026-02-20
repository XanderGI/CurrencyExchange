package io.github.XanderGI.mapper;

import io.github.XanderGI.dto.CurrencyRequestDto;
import io.github.XanderGI.model.Currency;

public class CurrencyMapper {
    public static CurrencyRequestDto toDto(String name, String code, String sign) {
        return new CurrencyRequestDto(
                name,
                code,
                sign
        );
    }

    public static Currency toModel(CurrencyRequestDto currencyRequestDto) {
        return new Currency(
                currencyRequestDto.name(),
                currencyRequestDto.code(),
                currencyRequestDto.sign()
        );
    }
}