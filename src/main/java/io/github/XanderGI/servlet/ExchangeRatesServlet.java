package io.github.XanderGI.servlet;

import io.github.XanderGI.dto.ErrorResponse;
import io.github.XanderGI.dto.ExchangeRateRequestDto;
import io.github.XanderGI.exception.CurrencyNotFoundException;
import io.github.XanderGI.exception.ExchangeRateAlreadyExistsException;
import io.github.XanderGI.mapper.ExchangeRateMapper;
import io.github.XanderGI.model.ExchangeRate;
import io.github.XanderGI.service.ExchangeRateService;
import io.github.XanderGI.utils.JsonMapper;
import io.github.XanderGI.utils.ValidationUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<ExchangeRate> exchangeRates = exchangeRateService.getAllExchangeRates();
            JsonMapper.sendJson(resp, exchangeRates, 200);
        } catch (Exception e) {
            JsonMapper.sendJson(resp, new ErrorResponse("Server error"), 500);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!ValidationUtils.isValid(req, "baseCurrencyCode", "targetCurrencyCode", "rate")) {
            JsonMapper.sendJson(resp, new ErrorResponse("The required form field is missing"), 400);
            return;
        }

        if (!ValidationUtils.areParametersValid(req, "baseCurrencyCode", "targetCurrencyCode")) {
            JsonMapper.sendJson(resp, new ErrorResponse("The currency codes of the exchangeRate incorrect in the address"), 400);
            return;
        }

        try {
            ExchangeRateRequestDto exchangeRateRequestDto = ExchangeRateMapper.toRequestDto(req);
            ExchangeRate exchangeRate = exchangeRateService.addExchangeRate(exchangeRateRequestDto);
            JsonMapper.sendJson(resp, exchangeRate, 201);
        } catch (NumberFormatException e) {
            JsonMapper.sendJson(resp, new ErrorResponse("Invalid format number"), 400);
        } catch (IllegalArgumentException e) {
            JsonMapper.sendJson(resp, new ErrorResponse(e.getMessage()), 400);
        } catch (CurrencyNotFoundException e) {
            JsonMapper.sendJson(resp, new ErrorResponse(e.getMessage()), 404);
        } catch (ExchangeRateAlreadyExistsException e) {
            JsonMapper.sendJson(resp, new ErrorResponse(e.getMessage()), 409);
        } catch (Exception e) {
            JsonMapper.sendJson(resp, new ErrorResponse("Server error"), 500);
        }
    }
}