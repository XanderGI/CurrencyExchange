package io.github.XanderGI.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class RequestUtils {

    private RequestUtils() {

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

            try {
                String decodedKey = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                if (parts.length == 2) {
                    String decodedValue = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                    map.putIfAbsent(decodedKey, decodedValue);
                } else {
                    map.putIfAbsent(decodedKey, "");
                }
            } catch (IllegalArgumentException e) {
                throw  new IllegalArgumentException("Bad request: invalid parameter encoding");
            }

        }
        return map;
    }
}