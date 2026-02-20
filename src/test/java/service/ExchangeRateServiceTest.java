package service;

import io.github.XanderGI.dao.impl.CurrencyDaoImpl;
import io.github.XanderGI.dao.impl.ExchangeRateDaoImpl;
import io.github.XanderGI.dto.ExchangeRateRequestConvertDto;
import io.github.XanderGI.dto.ExchangeRateResponseConvertDto;
import io.github.XanderGI.model.Currency;
import io.github.XanderGI.model.ExchangeRate;
import io.github.XanderGI.service.ExchangeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

    @Mock
    private CurrencyDaoImpl currencyDaoImpl;

    @InjectMocks
    private ExchangeService service;

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
        Currency currencyUSD = new Currency("United States Dollar", "USD", "$");

        ExchangeRate fromRubToUsdRate = new ExchangeRate(
                currencyUSD,
                new Currency("Russian Ruble", "RUB", "₽"),
                new BigDecimal("100")
        );

        ExchangeRate fromUsdToEurRate = new ExchangeRate(
                currencyUSD,
                new Currency("Euro", "EUR", "€"),
                new BigDecimal("0.5")
        );

        List<ExchangeRate> exchangeRates = List.of(fromRubToUsdRate, fromUsdToEurRate);

        when(exchangeRateDaoImpl.findAllUsdRelatedPairs(base, target)).thenReturn(exchangeRates);

        ExchangeRateResponseConvertDto respDto = service.convertCurrency(reqDto);

        assertNotNull(respDto);
        assertEquals(0, new BigDecimal("5").compareTo(respDto.convertedAmount()));
    }
}