package io.github.XanderGI.servlet;

import io.github.XanderGI.dto.CurrencyRequestDto;
import io.github.XanderGI.dto.ErrorResponse;
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
        currencyService = (CurrencyService) getServletContext().getAttribute("currencyService");
        mapper = (CurrencyMapper) getServletContext().getAttribute("currencyMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<Currency> currencies = currencyService.getAllCurrencies();
        JsonMapper.sendJson(resp, currencies, 200);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!ValidationUtils.hasRequiredFields(req, "name", "code", "sign")) {
            JsonMapper.sendJson(resp, new ErrorResponse("The required form field is missing"), 400);
            return;
        }

        String name = req.getParameter("name");
        String code = req.getParameter("code").toUpperCase();
        String sign = req.getParameter("sign");

        if (!ValidationUtils.isCodeValid(code)) {
            JsonMapper.sendJson(resp, new ErrorResponse("Currency code has an incorrect format"), 400);
            return;
        }

        CurrencyRequestDto currencyDto = mapper.toCurrencyRequest(name, code, sign);
        Currency currency = currencyService.addCurrency(currencyDto);

        JsonMapper.sendJson(resp, currency, 201);
    }
}