package io.github.XanderGI.servlet;

import io.github.XanderGI.dao.CurrencyDao;
import io.github.XanderGI.dao.ExchangeRateDao;
import io.github.XanderGI.dto.ErrorResponse;
import io.github.XanderGI.dto.ExchangeRateRequestDto;
import io.github.XanderGI.mapper.ExchangeRateMapper;
import io.github.XanderGI.model.ExchangeRate;
import io.github.XanderGI.service.ExchangeRateService;
import io.github.XanderGI.utils.JsonMapper;
import io.github.XanderGI.utils.ValidationUtils;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends BaseServlet {
    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        exchangeRateService = (ExchangeRateService) getServletContext().getAttribute("exchangeRateService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ExchangeRate> exchangeRates = exchangeRateService.getAllExchangeRates();
        JsonMapper.sendJson(resp, exchangeRates, 200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!ValidationUtils.hasRequiredFields(req, "baseCurrencyCode", "targetCurrencyCode", "rate")) {
            JsonMapper.sendJson(resp, new ErrorResponse("The required form field is missing"), 400);
            return;
        }

        String baseCode = req.getParameter("baseCurrencyCode").toUpperCase();
        String targetCode = req.getParameter("targetCurrencyCode").toUpperCase();
        String rate = req.getParameter("rate");

        if (!ValidationUtils.isCodeValid(baseCode) || !ValidationUtils.isCodeValid(targetCode)) {
            JsonMapper.sendJson(resp, new ErrorResponse("The currency codes of the exchangeRate incorrect in the body"), 400);
            return;
        }

        ExchangeRateRequestDto exchangeRateRequestDto = ExchangeRateMapper.toRequestDto(baseCode, targetCode, rate);
        ExchangeRate exchangeRate = exchangeRateService.addExchangeRate(exchangeRateRequestDto);

        JsonMapper.sendJson(resp, exchangeRate, 201);
    }
}