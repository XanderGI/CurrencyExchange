package io.github.XanderGI.servlet;

import io.github.XanderGI.model.Currency;
import io.github.XanderGI.service.CurrencyService;
import io.github.XanderGI.utils.JsonMapper;
import io.github.XanderGI.utils.ValidationUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends BaseServlet {
    private static final int LEADING_SLASH_OFFSET = 1;
    private CurrencyService currencyService;

    @Override
    public void init() {
        currencyService = (CurrencyService) getServletContext().getAttribute("currencyService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            throw new IllegalArgumentException("Currency code is missing in URL");
        }

        String codeCurrency = pathInfo.substring(LEADING_SLASH_OFFSET).toUpperCase();

        if (!ValidationUtils.isCodeValid(codeCurrency)) {
            throw new IllegalArgumentException("Currency code has an incorrect format");
        }

        Currency currency = currencyService.getCurrencyByCode(codeCurrency);

        JsonMapper.sendJson(resp, currency, HttpServletResponse.SC_OK);
    }
}