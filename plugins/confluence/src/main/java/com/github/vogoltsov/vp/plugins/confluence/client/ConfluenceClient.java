package com.github.vogoltsov.vp.plugins.confluence.client;

import com.github.vogoltsov.vp.plugins.confluence.client.model.Space;
import com.github.vogoltsov.vp.plugins.confluence.util.UnirestUtils;
import com.github.vogoltsov.vp.plugins.confluence.util.cql.CQLQuery;
import lombok.Getter;
import lombok.Setter;
import unirest.GetRequest;
import unirest.HttpRequestWithBody;
import unirest.UnirestInstance;

import java.nio.charset.StandardCharsets;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ConfluenceClient {

    public static ConfluenceClient getInstance() {
        return SingletonHolder.INSTANCE;
    }


    private final UnirestInstance unirest = UnirestUtils.configure();


    @Getter
    @Setter
    private String baseUrl;

    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;


    public ConfluenceClient() {
    }


    /**
     * Returns whether this client has been configured or not.
     */
    public boolean isConfigured() {
        return this.baseUrl != null && !this.baseUrl.isEmpty();
    }


    /**
     * Tests connection parameters by initiating a test search on Confluence server.
     */
    public void testConnection() {
        get("/rest/api/user/current")
                .asJson()
                .ifFailure(UnirestUtils::handleRequestFailure);
    }

    public Space findSpaceById(String key) {
        return get("/rest/api/space/{key}")
                .routeParam("key", key)
                .asObject(Space.class)
                .ifFailure(UnirestUtils::handleRequestFailure)
                .getBody();
    }


    GetRequest search(CQLQuery cql) {
        return get("/rest/api/search")
                .queryString("cql", cql.getQueryString())
                .queryString("expand", String.join(",", cql.getExpandedProperties()));
    }

    GetRequest get(String uri) {
        GetRequest request = unirest.get(this.baseUrl + uri);
        request.header("accept", "application/json");
        request.header("X-Atlassian-Token", "nocheck");
        if (username != null && !username.isEmpty() && password != null) {
            request.basicAuth(username, password);
        }
        return request;
    }

    HttpRequestWithBody post(String uri) {
        HttpRequestWithBody request = unirest.post(this.baseUrl + uri);
        request.charset(StandardCharsets.UTF_8);
        request.header("accept", "application/json");
        request.header("X-Atlassian-Token", "nocheck");
        if (username != null && !username.isEmpty() && password != null) {
            request.basicAuth(username, password);
        }
        return request;
    }


    private static class SingletonHolder {
        private static final ConfluenceClient INSTANCE = new ConfluenceClient();
    }

}
