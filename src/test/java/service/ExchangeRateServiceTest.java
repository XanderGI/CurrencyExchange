package service;

import io.github.XanderGI.dao.impl.ExchangeRateDaoImpl;
import io.github.XanderGI.dto.ExchangeRateRequestConvertDto;
import io.github.XanderGI.dto.ExchangeRateResponseConvertDto;
import io.github.XanderGI.mapper.ExchangeRateMapper;
import io.github.XanderGI.model.Currency;
import io.github.XanderGI.model.ExchangeRate;
import io.github.XanderGI.service.ExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateDaoImpl exchangeRateDaoImpl;

    private ExchangeRateMapper mapper;

    private ExchangeService service;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(ExchangeRateMapper.class);
        service = new ExchangeService(exchangeRateDaoImpl, mapper);
    }

    @Test
    void shouldConvertCurrencyUsingCrossRate() {
        String base = "RUB";
        String target = "EUR";
        BigDecimal amount = BigDecimal.valueOf(1000);
        ExchangeRateRequestConvertDto reqDto = new ExchangeRateRequestConvertDto(
                base,
                target,
                amount
        );
        Currency currencyUSD = new Currency(1L, "United States Dollar", "USD", "$");
        Currency currencyRUB = new Currency(2L, "Russian Ruble", "RUB", "₽");
        Currency currencyEUR = new Currency(3L, "Euro", "EUR", "€");
        ExchangeRate fromRubToUsdRate = new ExchangeRate(
                1L,
                currencyUSD,
                currencyRUB,
                new BigDecimal("100")
        );

        ExchangeRate fromUsdToEurRate = new ExchangeRate(
                2L,
                currencyUSD,
                currencyEUR,
                new BigDecimal("0.5")
        );
        List<ExchangeRate> exchangeRates = List.of(fromRubToUsdRate, fromUsdToEurRate);
        when(exchangeRateDaoImpl.findAllUsdRelatedPairs(base, target)).thenReturn(exchangeRates);

        ExchangeRateResponseConvertDto respDto = service.convertCurrency(reqDto);

        assertNotNull(respDto);
        assertEquals(0, new BigDecimal("5").compareTo(respDto.convertedAmount()));
        assertEquals(0, new BigDecimal("0.005").compareTo(respDto.rate()));
        assertEquals(0, new BigDecimal("1000").compareTo(respDto.amount()));
    }
}