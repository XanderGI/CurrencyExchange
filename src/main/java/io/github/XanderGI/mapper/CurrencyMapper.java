package io.github.XanderGI.mapper;

import io.github.XanderGI.dto.CurrencyRequestDto;
import io.github.XanderGI.model.Currency;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CurrencyMapper {
    CurrencyMapper INSTANCE = Mappers.getMapper(CurrencyMapper.class);

    CurrencyRequestDto toCurrencyRequest(String name, String code, String sign);
    @Mapping(source = "name", target = "fullName")
    @Mapping(target = "id", ignore = true)
    Currency toCurrencyModel(CurrencyRequestDto currencyRequestDto);
}