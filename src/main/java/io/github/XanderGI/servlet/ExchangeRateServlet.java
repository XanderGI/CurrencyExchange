package io.github.XanderGI.servlet;

import io.github.XanderGI.dao.CurrencyDao;
import io.github.XanderGI.dao.ExchangeRateDao;
import io.github.XanderGI.dto.CurrencyPair;
import io.github.XanderGI.dto.ErrorResponse;
import io.github.XanderGI.dto.ExchangeRateRequestDto;
import io.github.XanderGI.mapper.ExchangeRateMapper;
import io.github.XanderGI.model.ExchangeRate;
import io.github.XanderGI.service.ExchangeRateService;
import io.github.XanderGI.utils.JsonMapper;
import io.github.XanderGI.utils.ValidationUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import static io.github.XanderGI.utils.RequestUtils.getBodyParams;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends BaseServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService(new ExchangeRateDao(), new CurrencyDao());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();

        CurrencyPair currencyPair = extractCurrencies(path);
        ExchangeRate exchangeRate = exchangeRateService.getExchangeRateByCode(
                currencyPair.base(), currencyPair.target());

        JsonMapper.sendJson(resp, exchangeRate, 200);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();
        CurrencyPair currencyPair = extractCurrencies(path);
        Map<String, String> bodyParams = getBodyParams(req);
        String rateValue = bodyParams.get("rate");

        if (rateValue == null || rateValue.isEmpty()) {
            JsonMapper.sendJson(resp, new ErrorResponse("The required form field is missing"), 400);
            return;
        }

        ExchangeRateRequestDto exchangeRateRequestDto = ExchangeRateMapper.toRequestDto(
                currencyPair.base(), currencyPair.target(), rateValue);
        ExchangeRate exchangeRate = exchangeRateService.updateExchangeRate(exchangeRateRequestDto);

        JsonMapper.sendJson(resp, exchangeRate, 200);
    }

    private CurrencyPair extractCurrencies(String path) {
        if (path == null || path.equals("/")) {
            throw new IllegalArgumentException("Currency codes of the exchangeRate are missing in the address");
        }

        if (path.length() != 7) {
            throw new IllegalArgumentException("Currency codes of the exchangeRate are incorrect in the address");
        }

        path = path.toUpperCase();
        String baseCode = path.substring(1, 4);
        String targetCode = path.substring(4, 7);

        if (!(ValidationUtils.isCodeValid(baseCode) && ValidationUtils.isCodeValid(targetCode))) {
            throw new IllegalArgumentException("Currency code has an incorrect format");
        }

        return new CurrencyPair(baseCode, targetCode);
    }
}