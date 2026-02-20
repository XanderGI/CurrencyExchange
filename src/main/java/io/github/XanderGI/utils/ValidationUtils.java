package io.github.XanderGI.utils;

import io.github.XanderGI.dto.ExchangeRateRequestConvertDto;
import io.github.XanderGI.dto.ExchangeRateRequestDto;
import jakarta.servlet.http.HttpServletRequest;

public final class ValidationUtils {

    private ValidationUtils() {

    }

    public static boolean hasRequiredFields(HttpServletRequest req, String... requiredFields) {
        for (String field : requiredFields) {
            String value = req.getParameter(field);
            if (value == null || value.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isCodeValid(String code) {
        if (code == null || code.length() != 3) {
            return false;
        }

        for (char character : code.toCharArray()) {
            if (character < 'A' || character > 'Z') {
                return false;
            }
        }
        return true;
    }

    public static void validate(ExchangeRateRequestDto dto) {
        checkCurrencyCodesAreDifferent(dto.baseCurrencyCode(), dto.targetCurrencyCode());

        if (dto.rate().signum() <= 0) {
            throw new IllegalArgumentException("The rate value must be positive");
        }
    }

    public static void validate(ExchangeRateRequestConvertDto dto) {
        checkCurrencyCodesAreDifferent(dto.baseCurrencyCode(), dto.targetCurrencyCode());

        if (dto.amount().signum() <= 0) {
            throw new IllegalArgumentException("The amount value must be positive");
        }
    }

    public static void checkCurrencyCodesAreDifferent(String base, String target) {
        if (base.equals(target)) {
            throw new IllegalArgumentException("Currency codes should not be repeated");
        }
    }
}