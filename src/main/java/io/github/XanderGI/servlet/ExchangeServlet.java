package io.github.XanderGI.servlet;

import io.github.XanderGI.dto.ErrorResponse;
import io.github.XanderGI.dto.ExchangeRateRequestConvertDto;
import io.github.XanderGI.dto.ExchangeRateResponseConvertDto;
import io.github.XanderGI.mapper.ExchangeRateMapper;
import io.github.XanderGI.service.ExchangeService;
import io.github.XanderGI.utils.JsonMapper;
import io.github.XanderGI.utils.ValidationUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends BaseServlet {
    private ExchangeService exchangeService;

    @Override
    public void init() {
        exchangeService = (ExchangeService) getServletContext().getAttribute("exchangeService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!ValidationUtils.hasRequiredFields(req, "to", "from", "amount")) {
            JsonMapper.sendJson(resp, new ErrorResponse("The required form field is missing"), 400);
            return;
        }

        String from = req.getParameter("from").toUpperCase();
        String to = req.getParameter("to").toUpperCase();
        String amount = req.getParameter("amount");

        if (!ValidationUtils.isCodeValid(from) || !ValidationUtils.isCodeValid(to)) {
            JsonMapper.sendJson(resp, new ErrorResponse("The currency codes of the exchangeRate incorrect in the address"), 400);
            return;
        }

        ExchangeRateRequestConvertDto reqDto = ExchangeRateMapper.toConvertDto(from, to, amount);
        ExchangeRateResponseConvertDto respDto = exchangeService.convertCurrency(reqDto);

        JsonMapper.sendJson(resp, respDto, 200);
    }
}