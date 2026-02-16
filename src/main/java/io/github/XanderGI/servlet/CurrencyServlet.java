package io.github.XanderGI.servlet;

import io.github.XanderGI.dto.ErrorResponse;
import io.github.XanderGI.exception.CurrencyNotFoundException;
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

// todo: done забыл проверку на длину кода символа.

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = new CurrencyService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            JsonMapper.sendJson(resp, new ErrorResponse("Currency code is missing in URL"),400 );
            return;
        }

        String codeCurrency = pathInfo.substring(1).toUpperCase();

        if (!ValidationUtils.isCodeValid(codeCurrency)) {
            JsonMapper.sendJson(resp, new ErrorResponse("Currency code has an incorrect format"), 400);
            return;
        }

        try {
            Currency currency = currencyService.getCurrencyByCode(codeCurrency);
            JsonMapper.sendJson(resp, currency, 200);
        } catch (CurrencyNotFoundException e) {
            JsonMapper.sendJson(resp, new ErrorResponse(e.getMessage()), 404);
        } catch (Exception e) {
            JsonMapper.sendJson(resp, new ErrorResponse("Server error"), 500);
        }
    }
}