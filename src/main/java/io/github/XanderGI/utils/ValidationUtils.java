package io.github.XanderGI.utils;

import io.github.XanderGI.dto.ExchangeRateRequestConvertDto;
import io.github.XanderGI.dto.ExchangeRateRequestDto;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public final class ValidationUtils {
    private static final int MAX_CURRENCY_NAME_LENGTH = 50;
    private static final int CURRENCY_CODE_LENGTH = 3;
    private static final int MAX_SIGN_LENGTH = 3;

    private ValidationUtils() {

    }

    public static void checkRequiredFields(HttpServletRequest req, String... requiredFields) {
        for (String field : requiredFields) {
            String value = req.getParameter(field);
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("The required form field is missing: " + field);
            }
        }
    }

    public static void checkNameIsValid(String name) {
        if (name == null || name.isBlank() || name.length() > MAX_CURRENCY_NAME_LENGTH) {
            throw new IllegalArgumentException("Currency name is invalid or exceeds the maximum length of " + MAX_CURRENCY_NAME_LENGTH + " characters");
        }
    }

    public static void checkCodeIsValid(String code) {
        if (code == null || code.length() != CURRENCY_CODE_LENGTH) {
            throw new IllegalArgumentException("Currency code has an incorrect format");
        }

        for (char character : code.toCharArray()) {
            if (character < 'A' || character > 'Z') {
                throw new IllegalArgumentException("Currency code has an incorrect format");
            }
        }
    }

    public static void checkStringNotBlank(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("The required form field is missing");
        }
    }

    public static void checkSignIsValid(String sign) {
        if (sign == null || sign.isBlank() || sign.length() > MAX_SIGN_LENGTH) {
            throw new IllegalArgumentException("Currency sign has an incorrect format");
        }
    }

    public static void checkPathIsValid(String path, String errorMessage) {
        if (path == null || path.equals("/")) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void validate(ExchangeRateRequestDto dto) {
        checkCurrencyCodesAreDifferent(dto.baseCurrencyCode(), dto.targetCurrencyCode());

        if (isNotPositive(dto.rate())) {
            throw new IllegalArgumentException("The rate value must be positive");
        }
    }

    public static void validate(ExchangeRateRequestConvertDto dto) {
        checkCurrencyCodesAreDifferent(dto.baseCurrencyCode(), dto.targetCurrencyCode());

        if (isNotPositive(dto.amount())) {
            throw new IllegalArgumentException("The amount value must be positive");
        }
    }

    public static void checkCurrencyCodesAreDifferent(String base, String target) {
        if (base != null && base.equals(target)) {
            throw new IllegalArgumentException("Currency codes should not be repeated");
        }
    }

    private static boolean isNotPositive(BigDecimal value) {
        return value == null || value.signum() <= 0;
    }
}