package io.github.XanderGI.servlet;

import io.github.XanderGI.dto.ErrorResponse;
import io.github.XanderGI.dto.ExchangeRateRequestConvertDto;
import io.github.XanderGI.dto.ExchangeRateResponseConvertDto;
import io.github.XanderGI.exception.ExchangeRateNotFoundException;
import io.github.XanderGI.mapper.ExchangeRateMapper;
import io.github.XanderGI.service.ExchangeRateService;
import io.github.XanderGI.utils.JsonMapper;
import io.github.XanderGI.utils.ValidationUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!ValidationUtils.isValid(req, "to", "from", "amount")) {
            JsonMapper.sendJson(resp, new ErrorResponse("The required form field is missing"), 400);
            return;
        }

        if (!ValidationUtils.areParametersValid(req, "to", "from")) {
            JsonMapper.sendJson(resp, new ErrorResponse("The currency codes of the exchangeRate incorrect in the address"), 400);
            return;
        }

        try {
            ExchangeRateRequestConvertDto reqDto = ExchangeRateMapper.toConvertDto(req);
            ExchangeRateResponseConvertDto respDto = exchangeRateService.convertCurrency(reqDto);
            JsonMapper.sendJson(resp, respDto, 200);
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
}