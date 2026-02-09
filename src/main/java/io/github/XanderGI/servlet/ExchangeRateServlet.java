package io.github.XanderGI.servlet;

import io.github.XanderGI.dto.ErrorResponse;
import io.github.XanderGI.exception.ExchangeRateNotFoundException;
import io.github.XanderGI.model.ExchangeRate;
import io.github.XanderGI.service.ExchangeRateService;
import io.github.XanderGI.utils.JsonMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.length() != 7) {
            JsonMapper.sendJson(resp, new ErrorResponse("The currency codes of the exchangeRate are missing in the address"), 400);
            return;
        }
        pathInfo = pathInfo.toUpperCase();

        String baseCode = pathInfo.substring(1,4);
        String targetCode = pathInfo.substring(4,7);

        try {
            ExchangeRate exchangeRate = exchangeRateService.getExchangeRateByCode(baseCode, targetCode);
            JsonMapper.sendJson(resp, exchangeRate, 200);
        } catch (ExchangeRateNotFoundException e) {
            JsonMapper.sendJson(resp, new ErrorResponse(e.getMessage()), 404);
        } catch (Exception e) {
            JsonMapper.sendJson(resp, new ErrorResponse("Server error"), 500);
        }
    }
}