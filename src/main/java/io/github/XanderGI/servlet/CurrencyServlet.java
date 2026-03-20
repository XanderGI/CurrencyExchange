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
        String path = req.getPathInfo();

        ValidationUtils.checkPathIsValid(path, "Currency code is missing in URL");

        String codeCurrency = path.substring(LEADING_SLASH_OFFSET).toUpperCase();

        ValidationUtils.checkCodeIsValid(codeCurrency);

        Currency currency = currencyService.getCurrencyByCode(codeCurrency);

        JsonMapper.sendJson(resp, currency, HttpServletResponse.SC_OK);
    }
}