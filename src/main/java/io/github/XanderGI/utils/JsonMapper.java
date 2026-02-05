package io.github.XanderGI.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public final class JsonMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonMapper() {

    }

    public static String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting to JSON", e);
        }
    }

    public static void sendJson(HttpServletResponse resp, Object object, int status) throws IOException {
        resp.setContentType("application/json");
        resp.setStatus(status);
        resp.getWriter().write(toJson(object));
    }
}