package io.github.XanderGI.utils;

import jakarta.servlet.http.HttpServletRequest;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class ValidationUtils {

    private ValidationUtils() {

    }

    public static boolean isValid(HttpServletRequest req, String... requiredFields) {
        for (String field : requiredFields) {
            String value = req.getParameter(field);
            if (value == null || value.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidCurrencyCodes(HttpServletRequest req, String... requiredFields) {
        for (String field : requiredFields) {
            String code = req.getParameter(field);
            if (code.length() != 3) {
                return false;
            }
        }
        return true;
    }

    public static Map<String, String> parseBodyParams(String body) {
        Map<String, String> map = new HashMap<>();

        if (body.isEmpty()) {
            return map;
        }

        String[] pairs = body.split("&");
        for (String element : pairs) {

            if (element.isEmpty()) {
                continue;
            }

            String[] parts = element.split("=", 2);
            String decodedKey = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
            if (parts.length == 2) {
                String decodedValue = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                map.putIfAbsent(decodedKey, decodedValue);
            } else {
                map.putIfAbsent(decodedKey, "");
            }
        }
        return map;
    }
}