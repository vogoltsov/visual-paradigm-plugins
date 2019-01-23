package com.github.vogoltsov.vp.plugins.confluence.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import unirest.Config;
import unirest.HttpResponse;
import unirest.JacksonObjectMapper;
import unirest.UnirestInstance;

import java.nio.charset.StandardCharsets;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class UnirestUtils {

    public static UnirestInstance configure() {
        Config config = new Config();
        config.setObjectMapper(new JacksonObjectMapper(objectMapper()));
        config.setDefaultHeader("accept", "application/json");
        config.setDefaultResponseEncoding(StandardCharsets.UTF_8.name());
        return new UnirestInstance(config);
    }

    public static ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    public static <T> void handleRequestFailure(HttpResponse<T> response) {
        String message = "Request failed with HTTP error code " + response.getStatus();
        String statusText = response.getStatusText();
        if (statusText != null && !statusText.isEmpty()) {
            message += ": " + statusText;
        }
        throw new RuntimeException(message);
    }

    public static void handleFailureResponse(HttpResponse<JsonNode> response) {
        String message = null;
        // parse confluence error format
        if (response.getBody() != null && response.getBody().has("statusCode")) {
            try {
                message = response.getBody().get("message").textValue();
            } catch (Exception e) {
                /* incorrect error format */
            }
        }
        // default http error
        if (message == null) {
            message = "Request failed with HTTP error code " + response.getStatus();
            String statusText = response.getStatusText();
            if (statusText != null && !statusText.isEmpty()) {
                message += ": " + statusText;
            }
        }
        throw new RuntimeException(message);
    }


    private UnirestUtils() {
    }

}
