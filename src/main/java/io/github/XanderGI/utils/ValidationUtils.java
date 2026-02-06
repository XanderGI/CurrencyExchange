package io.github.XanderGI.utils;

import jakarta.servlet.http.HttpServletRequest;

public class ValidationUtils {
    public static boolean isValid(HttpServletRequest req, String... requiredFields) {
        for (String field : requiredFields) {
            String value = req.getParameter(field);
            if (value == null || value.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}