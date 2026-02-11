package io.github.XanderGI.servlet;

import io.github.XanderGI.dto.ErrorResponse;
import io.github.XanderGI.dto.ExchangeRateRequestDto;
import io.github.XanderGI.exception.ExchangeRateNotFoundException;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

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

        String baseCode = pathInfo.substring(1, 4);
        String targetCode = pathInfo.substring(4, 7);

        try {
            ExchangeRate exchangeRate = exchangeRateService.getExchangeRateByCode(baseCode, targetCode);
            JsonMapper.sendJson(resp, exchangeRate, 200);
        } catch (ExchangeRateNotFoundException e) {
            JsonMapper.sendJson(resp, new ErrorResponse(e.getMessage()), 404);
        } catch (Exception e) {
            JsonMapper.sendJson(resp, new ErrorResponse("Server error"), 500);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.length() != 7) {
            JsonMapper.sendJson(resp, new ErrorResponse("The required form field is missing"), 400);
            return;
        }

        pathInfo = pathInfo.toUpperCase();
        String baseCode = pathInfo.substring(1, 4);
        String targetCode = pathInfo.substring(4, 7);

        try {
            Map<String, String> bodyParams = getBodyParams(req);
            String rateValue = bodyParams.get("rate");

            if (rateValue == null || rateValue.isEmpty()) {
                JsonMapper.sendJson(resp, new ErrorResponse("The required form field is missing"), 400);
                return;
            }

            ExchangeRateRequestDto exchangeRateRequestDto = ExchangeRateMapper.toDtoFromPatchRequest(baseCode, targetCode, rateValue);
            ExchangeRate exchangeRate = exchangeRateService.updateExchangeRate(exchangeRateRequestDto);
            JsonMapper.sendJson(resp, exchangeRate, 200);
        } catch (NumberFormatException e) {
            JsonMapper.sendJson(resp, new ErrorResponse("Invalid format number"), 400);
        } catch (IllegalArgumentException e) {
            JsonMapper.sendJson(resp, new ErrorResponse(e.getMessage()), 400);
        } catch (ExchangeRateNotFoundException e) {
            JsonMapper.sendJson(resp, new ErrorResponse(e.getMessage()), 404);
        } catch (Exception e) {
            JsonMapper.sendJson(resp, new ErrorResponse("Server error"), 500);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (!method.equals("PATCH")) {
            super.service(req, resp);
            return;
        }

        this.doPatch(req, resp);
    }

    private Map<String, String> getBodyParams(HttpServletRequest req) throws IOException {
        StringBuilder parameters = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                parameters.append(line);
            }
            return ValidationUtils.parseBodyParams(parameters.toString());
        }
    }
}