package com.github.vogoltsov.vp.plugins.confluence.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.github.vogoltsov.vp.plugins.confluence.util.RemoteAPIException;
import com.github.vogoltsov.vp.plugins.confluence.util.RemoteParseException;
import com.github.vogoltsov.vp.plugins.confluence.util.cql.CQLQuery;
import kong.unirest.Config;
import kong.unirest.GetRequest;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.UnirestInstance;
import kong.unirest.jackson.JacksonObjectMapper;
import lombok.Getter;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ConfluenceClient {

    public static ConfluenceClient getInstance() {
        return SingletonHolder.INSTANCE;
    }


    @Getter
    private final ObjectMapper objectMapper;

    @Getter
    private volatile UnirestInstance unirest;


    @Getter
    private String baseUrl;
    @Getter
    private boolean verifySsl = true;

    @Getter
    private String username;
    @Getter
    private String password;


    public ConfluenceClient() {
        this.objectMapper = initObjectMapper();
        this.unirest = initUnirest();
    }

    private ObjectMapper initObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    private UnirestInstance initUnirest() {
        Config config = new Config();
        config.defaultBaseUrl(this.baseUrl);
        config.verifySsl(this.verifySsl);
        if (this.username != null && !this.username.isEmpty() && this.password != null) {
            config.setDefaultBasicAuth(this.username, this.password);
        }
        config.setObjectMapper(new JacksonObjectMapper(this.objectMapper));
        config.setDefaultHeader("accept", "application/json");
        config.setDefaultHeader("X-Atlassian-Token", "nocheck");
        config.setDefaultResponseEncoding(StandardCharsets.UTF_8.name());
        return new UnirestInstance(config);
    }


    /**
     * Returns whether this client has been configured or not.
     */
    public boolean isConfigured() {
        return this.baseUrl != null && !this.baseUrl.isEmpty();
    }

    /**
     * Reconfigures client with new settings.
     */
    public void configure(String baseUrl, boolean verifySsl, String username, String password) {
        this.baseUrl = baseUrl;
        this.verifySsl = verifySsl;
        this.username = username;
        this.password = password;
        // create new unirest instance
        this.unirest = initUnirest();
    }


    /**
     * Tests connection parameters by initiating a test search on Confluence server.
     */
    public void testConnection() {
        get("/rest/api/user/current")
                .asObject(JsonNode.class)
                .ifFailure(this::handleFailureResponse);
    }


    /**
     * Creates a generic Confluence search request.
     */
    GetRequest search(CQLQuery cql) {
        return get("/rest/api/search")
                .queryString("cql", cql.getQueryString())
                .queryString("expand", String.join(",", cql.getExpandedProperties()));
    }

    /**
     * Creates a generic Confluence GET request.
     */
    GetRequest get(String uri) {
        return unirest.get(uri);
    }

    /**
     * Creates a generic Confluence POST request.
     */
    HttpRequestWithBody post(String uri) {
        HttpRequestWithBody request = unirest.post(uri);
        request.charset(StandardCharsets.UTF_8);
        return request;
    }


    /**
     * Performs general failure handling for Confluence API responses.
     */
    void handleFailureResponse(HttpResponse<JsonNode> response) {
        // parse confluence error format
        if (response.getBody() != null && response.getBody().has("statusCode")) {
            throw new RemoteAPIException(
                    response.getBody().get("statusCode").intValue(),
                    Optional.ofNullable(response.getBody())
                            .map(body -> body.get("message"))
                            .map(JsonNode::textValue)
                            .orElse(null),
                    Optional.ofNullable(response.getBody())
                            .map(body -> body.get("reason"))
                            .map(JsonNode::textValue)
                            .orElse(null)
            );
        }
        // default http error
        String message = "Request failed with HTTP error code " + response.getStatus();
        String statusText = response.getStatusText();
        if (statusText != null && !statusText.isEmpty()) {
            message += ": " + statusText;
        }
        throw new RuntimeException(message);
    }

    <T> Function<JsonNode, T> map(Class<T> clazz) {
        return jsonNode -> {
            try {
                return this.objectMapper.treeToValue(jsonNode, clazz);
            } catch (JsonProcessingException e) {
                throw new RemoteParseException(e);
            }
        };
    }


    private static class SingletonHolder {
        private static final ConfluenceClient INSTANCE = new ConfluenceClient();
    }

}
