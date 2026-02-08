package io.github.XanderGI.servlet;

import io.github.XanderGI.dto.CurrencyRequestDto;
import io.github.XanderGI.dto.ErrorResponse;
import io.github.XanderGI.exception.CurrencyAlreadyExistsException;
import io.github.XanderGI.mapper.CurrencyMapper;
import io.github.XanderGI.model.Currency;
import io.github.XanderGI.service.CurrencyService;
import io.github.XanderGI.utils.JsonMapper;
import io.github.XanderGI.utils.ValidationUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Currency> currencies = currencyService.getAllCurrencies();
            JsonMapper.sendJson(resp, currencies, 200);
        } catch (Exception e) {
            JsonMapper.sendJson(resp, new ErrorResponse("Server error"), 500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!ValidationUtils.isValid(req, "name", "code", "sign")) {
            JsonMapper.sendJson(resp, new ErrorResponse("The required form field is missing"), 400);
            return;
        }

        try {
            CurrencyRequestDto currencyDto = CurrencyMapper.toDto(req);
            Currency currency = currencyService.addCurrency(currencyDto);
            JsonMapper.sendJson(resp, currency, 201);
        } catch (CurrencyAlreadyExistsException e) {
            JsonMapper.sendJson(resp, new ErrorResponse(e.getMessage()), 409);
        } catch (Exception e) {
            JsonMapper.sendJson(resp, new ErrorResponse("Server error"), 500);
        }
    }
}