package io.github.XanderGI.servlet;

import io.github.XanderGI.dto.CurrencyRequestDto;
import io.github.XanderGI.listener.ContextListener;
import io.github.XanderGI.mapper.CurrencyMapper;
import io.github.XanderGI.model.Currency;
import io.github.XanderGI.service.CurrencyService;
import io.github.XanderGI.utils.JsonMapper;
import io.github.XanderGI.utils.ValidationUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends BaseServlet {
    private CurrencyService currencyService;
    private CurrencyMapper mapper;

    @Override
    public void init() {
        currencyService = (CurrencyService) getServletContext().getAttribute(ContextListener.CURRENCY_SERVICE_ATTRIBUTE);
        mapper = (CurrencyMapper) getServletContext().getAttribute(ContextListener.CURRENCY_MAPPER_ATTRIBUTE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Currency> currencies = currencyService.getAllCurrencies();
        JsonMapper.sendJson(resp, currencies, HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ValidationUtils.checkRequiredFields(req, "name", "code", "sign");

        String name = req.getParameter("name");
        String code = req.getParameter("code").toUpperCase();
        String sign = req.getParameter("sign");

        ValidationUtils.checkCodeIsValid(code);
        ValidationUtils.checkSignIsValid(sign);

        CurrencyRequestDto currencyDto = mapper.toCurrencyRequest(name, code, sign);
        Currency currency = currencyService.addCurrency(currencyDto);

        JsonMapper.sendJson(resp, currency, HttpServletResponse.SC_CREATED);
    }
}