package io.github.XanderGI.mapper;

import io.github.XanderGI.dto.ExchangeRateRequestConvertDto;
import io.github.XanderGI.dto.ExchangeRateRequestDto;
import io.github.XanderGI.dto.ExchangeRateResponseConvertDto;
import io.github.XanderGI.model.Currency;
import io.github.XanderGI.model.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper
public interface ExchangeRateMapper {
    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(target = "id", ignore = true)
    ExchangeRate toExchangeRateModel(ExchangeRateRequestDto dto, Currency baseCurrency, Currency targetCurrency);
    ExchangeRateRequestDto toRequestDto(String baseCurrencyCode, String targetCurrencyCode, String rate);
    ExchangeRateRequestConvertDto toConvertDto(String baseCurrencyCode, String targetCurrencyCode, String amount);
    ExchangeRateResponseConvertDto toResponseDto(Currency baseCurrency, Currency targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount);
}