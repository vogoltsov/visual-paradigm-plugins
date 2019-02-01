package com.github.vogoltsov.vp.plugins.confluence.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.vogoltsov.vp.plugins.confluence.client.dto.DataPage;
import com.github.vogoltsov.vp.plugins.confluence.client.dto.SearchResult;
import com.github.vogoltsov.vp.plugins.confluence.client.dto.SearchResults;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Space;
import com.github.vogoltsov.vp.plugins.confluence.util.RemoteAPIException;
import com.github.vogoltsov.vp.plugins.confluence.util.cql.CQL;
import com.github.vogoltsov.vp.plugins.confluence.util.cql.CQLQuery;

import java.util.Objects;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ConfluenceSpaceRepository {

    public static ConfluenceSpaceRepository getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * Searches all spaces by title and key.
     */
    public DataPage<Space> search(String text) {
        CQLQuery cql = CQL.query(
                CQL.eq("type", "space")
        );
        if (text != null && !text.isEmpty()) {
            cql.and(CQL.or(
                    CQL.like("space", text),
                    CQL.like("space", text + "*"),
                    CQL.eq("space.key", text)
            ));
        }
        cql.orderBy("title");
        return search(cql);
    }

    /**
     * Returns space by key, if any.
     */
    public Space findByKey(String spaceKey) {
        if (spaceKey == null) {
            return null;
        }
        return search(
                CQL.query(CQL.and(
                        CQL.eq("type", "space"),
                        CQL.eq("space.key", spaceKey)
                ))
        ).getResults().stream().findAny().orElse(null);
    }


    private DataPage<Space> search(CQLQuery cql) {
        try {
            return ConfluenceClient.getInstance().search(cql)
                    .asObject(JsonNode.class)
                    .ifFailure(ConfluenceClient.getInstance()::handleFailureResponse)
                    .mapBody(ConfluenceClient.getInstance().map(SearchResults.class).andThen(
                            searchResults -> searchResults.map(
                                    searchResult -> ((SearchResult.SpaceSearchResult) searchResult).getSpace()
                            )
                    ));
        } catch (RemoteAPIException e) {
            // this is a special case to mitigate bug in Confluence REST API
            // see https://jira.atlassian.com/browse/CONFSERVER-55445
            if (Objects.equals(e.getApiMessage(), "java.lang.IllegalArgumentException: parameters should not be empty")) {
                return DataPage.empty();
            }
            throw e;
        }
    }


    private static class SingletonHolder {
        private static final ConfluenceSpaceRepository INSTANCE = new ConfluenceSpaceRepository();
    }

}
