package com.github.vogoltsov.vp.plugins.confluence.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.vogoltsov.vp.plugins.confluence.client.dto.DataPage;
import com.github.vogoltsov.vp.plugins.confluence.client.dto.SearchResult;
import com.github.vogoltsov.vp.plugins.confluence.client.dto.SearchResults;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Page;
import com.github.vogoltsov.vp.plugins.confluence.util.cql.CQL;
import com.github.vogoltsov.vp.plugins.confluence.util.cql.CQLQuery;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ConfluencePageRepository {

    public static ConfluencePageRepository getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * Searches space pages by title.
     */
    public DataPage<Page> findBySpaceKeyAndText(String spaceKey, String text) {
        CQLQuery cql = CQL.query(CQL.eq("type", "page"));
        if (spaceKey != null && !spaceKey.isEmpty()) {
            cql.and(CQL.eq("space.key", spaceKey));
        }
        if (text != null && !text.isEmpty()) {
            cql.and(CQL.or(
                    CQL.like("title", text),
                    CQL.like("title", text + "*")
            ));
        }
        cql.orderBy("title");
        return search(cql);
    }

    /**
     * Returns page by id, if any.
     */
    public Page findById(String pageId) {
        if (pageId == null) {
            return null;
        }
        return search(
                CQL.query(CQL.and(
                        CQL.eq("type", "page"),
                        CQL.eq("id", pageId)
                ))
        ).getResults().stream().findAny().orElse(null);
    }


    private DataPage<Page> search(CQLQuery cql) {
        // always expand content space
        cql.expand("content.space");
        return ConfluenceClient.getInstance().search(cql)
                .asObject(JsonNode.class)
                .ifFailure(ConfluenceClient.getInstance()::handleFailureResponse)
                .mapBody(ConfluenceClient.getInstance().map(SearchResults.class).andThen(
                        searchResults -> searchResults.map(
                                searchResult -> (Page) ((SearchResult.ContentSearchResult) searchResult).getContent()
                        )
                ));
    }


    private static class SingletonHolder {
        private static final ConfluencePageRepository INSTANCE = new ConfluencePageRepository();
    }

}
