package io.github.XanderGI.servlet;

import io.github.XanderGI.dto.CurrencyPair;
import io.github.XanderGI.dto.ExchangeRateRequestDto;
import io.github.XanderGI.listener.ContextListener;
import io.github.XanderGI.mapper.ExchangeRateMapper;
import io.github.XanderGI.model.ExchangeRate;
import io.github.XanderGI.service.ExchangeRateService;
import io.github.XanderGI.utils.JsonMapper;
import io.github.XanderGI.utils.ValidationUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import static io.github.XanderGI.utils.RequestUtils.getBodyParams;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends BaseServlet {
    private static final int LEADING_SLASH_OFFSET = 1;
    private static final int CURRENCY_CODE_LENGTH = 3;
    private static final int FULL_PATH_LENGTH = 1 + 2 * CURRENCY_CODE_LENGTH;
    private ExchangeRateService exchangeRateService;
    private ExchangeRateMapper mapper;

    @Override
    public void init() {
        exchangeRateService = (ExchangeRateService) getServletContext().getAttribute(ContextListener.EXCHANGE_RATE_SERVICE_ATTRIBUTE);
        mapper = (ExchangeRateMapper) getServletContext().getAttribute(ContextListener.EXCHANGE_RATE_MAPPER_ATTRIBUTE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();

        CurrencyPair currencyPair = extractCurrencies(path);
        ExchangeRate exchangeRate = exchangeRateService.getExchangeRateByCode(
                currencyPair.base(), currencyPair.target());

        JsonMapper.sendJson(resp, exchangeRate, HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        CurrencyPair currencyPair = extractCurrencies(path);
        Map<String, String> bodyParams = getBodyParams(req);
        String rateValue = bodyParams.get("rate");

        ValidationUtils.checkStringNotBlank(rateValue);

        ExchangeRateRequestDto exchangeRateRequestDto = mapper.toRequestDto(
                currencyPair.base(), currencyPair.target(), rateValue);
        ExchangeRate exchangeRate = exchangeRateService.updateExchangeRate(exchangeRateRequestDto);

        JsonMapper.sendJson(resp, exchangeRate, HttpServletResponse.SC_OK);
    }

    private CurrencyPair extractCurrencies(String path) {
        ValidationUtils.checkPathIsValid(path, "Currency codes of the exchangeRate are missing in the address");

        if (path.length() != FULL_PATH_LENGTH) {
            throw new IllegalArgumentException("Currency codes of the exchangeRate are incorrect in the address");
        }

        int targetStart = LEADING_SLASH_OFFSET + CURRENCY_CODE_LENGTH;

        path = path.toUpperCase();
        String baseCode = path.substring(LEADING_SLASH_OFFSET, targetStart);
        String targetCode = path.substring(targetStart, targetStart + CURRENCY_CODE_LENGTH);

        ValidationUtils.checkCodeIsValid(baseCode);
        ValidationUtils.checkCodeIsValid(targetCode);

        return new CurrencyPair(baseCode, targetCode);
    }
}