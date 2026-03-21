package io.github.XanderGI.servlet;

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
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends BaseServlet {
    private ExchangeRateService exchangeRateService;
    private ExchangeRateMapper mapper;

    @Override
    public void init() {
        exchangeRateService = (ExchangeRateService) getServletContext().getAttribute(ContextListener.EXCHANGE_RATE_SERVICE_ATTRIBUTE);
        mapper = (ExchangeRateMapper) getServletContext().getAttribute(ContextListener.EXCHANGE_RATE_MAPPER_ATTRIBUTE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRate> exchangeRates = exchangeRateService.getAllExchangeRates();
        JsonMapper.sendJson(resp, exchangeRates, HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ValidationUtils.checkRequiredFields(req, "baseCurrencyCode", "targetCurrencyCode", "rate");

        String baseCode = req.getParameter("baseCurrencyCode").toUpperCase();
        String targetCode = req.getParameter("targetCurrencyCode").toUpperCase();
        String rate = req.getParameter("rate");

        ValidationUtils.checkCodeIsValid(baseCode);
        ValidationUtils.checkCodeIsValid(targetCode);

        ExchangeRateRequestDto exchangeRateRequestDto = mapper.toRequestDto(baseCode, targetCode, rate);
        ExchangeRate exchangeRate = exchangeRateService.addExchangeRate(exchangeRateRequestDto);

        JsonMapper.sendJson(resp, exchangeRate, HttpServletResponse.SC_CREATED);
    }
}