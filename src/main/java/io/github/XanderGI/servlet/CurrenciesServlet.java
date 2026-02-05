package io.github.XanderGI.servlet;

import io.github.XanderGI.dto.ErrorResponse;
import io.github.XanderGI.model.Currency;
import io.github.XanderGI.service.CurrencyService;
import io.github.XanderGI.utils.JsonMapper;
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
}