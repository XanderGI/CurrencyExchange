package io.github.XanderGI.servlet;

import io.github.XanderGI.dto.ExchangeRateRequestConvertDto;
import io.github.XanderGI.dto.ExchangeRateResponseConvertDto;
import io.github.XanderGI.listener.ContextListener;
import io.github.XanderGI.mapper.ExchangeRateMapper;
import io.github.XanderGI.service.ExchangeService;
import io.github.XanderGI.utils.JsonMapper;
import io.github.XanderGI.utils.ValidationUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends BaseServlet {
    private ExchangeService exchangeService;
    private ExchangeRateMapper mapper;

    @Override
    public void init() {
        exchangeService = (ExchangeService) getServletContext().getAttribute(ContextListener.EXCHANGE_SERVICE_ATTRIBUTE);
        mapper = (ExchangeRateMapper) getServletContext().getAttribute(ContextListener.EXCHANGE_RATE_MAPPER_ATTRIBUTE);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ValidationUtils.checkRequiredFields(req, "to", "from", "amount");

        String from = req.getParameter("from").toUpperCase();
        String to = req.getParameter("to").toUpperCase();
        String amount = req.getParameter("amount");

        ValidationUtils.checkCodeIsValid(from);
        ValidationUtils.checkCodeIsValid(to);

        ExchangeRateRequestConvertDto reqDto = mapper.toConvertDto(from, to, amount);
        ExchangeRateResponseConvertDto respDto = exchangeService.convertCurrency(reqDto);

        JsonMapper.sendJson(resp, respDto, HttpServletResponse.SC_OK);
    }
}