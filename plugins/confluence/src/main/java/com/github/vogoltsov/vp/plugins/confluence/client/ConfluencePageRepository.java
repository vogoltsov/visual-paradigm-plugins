package com.github.vogoltsov.vp.plugins.confluence.client;

import com.github.vogoltsov.vp.plugins.confluence.client.dto.SearchResult;
import com.github.vogoltsov.vp.plugins.confluence.client.dto.SearchResults;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Page;
import com.github.vogoltsov.vp.plugins.confluence.util.UnirestUtils;
import com.github.vogoltsov.vp.plugins.confluence.util.cql.CQL;
import com.github.vogoltsov.vp.plugins.confluence.util.cql.CQLQuery;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<Page> findBySpaceKeyAndText(String spaceKey, String text) {
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
        ).stream().findAny().orElse(null);
    }


    private List<Page> search(CQLQuery cql) {
        // always expand content space
        cql.expand("content.space");
        return ConfluenceClient.getInstance().search(cql)
                .asObject(SearchResults.class)
                .ifFailure(UnirestUtils::handleRequestFailure)
                .mapBody(
                        searchResults -> searchResults.getResults().stream()
                                .filter(SearchResult.ContentSearchResult.class::isInstance)
                                .map(searchResult -> ((SearchResult.ContentSearchResult) searchResult).getContent())
                                .filter(Page.class::isInstance)
                                .map(content -> (Page) content)
                                .collect(Collectors.toList())
                );
    }


    private static class SingletonHolder {
        private static final ConfluencePageRepository INSTANCE = new ConfluencePageRepository();
    }

}
