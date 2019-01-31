package com.github.vogoltsov.vp.plugins.confluence.client;

import com.github.vogoltsov.vp.plugins.confluence.client.dto.SearchResult;
import com.github.vogoltsov.vp.plugins.confluence.client.dto.SearchResults;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Space;
import com.github.vogoltsov.vp.plugins.confluence.util.UnirestUtils;
import com.github.vogoltsov.vp.plugins.confluence.util.cql.CQL;
import com.github.vogoltsov.vp.plugins.confluence.util.cql.CQLQuery;

import java.util.List;
import java.util.stream.Collectors;

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
    public List<Space> search(String text) {
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
        ).stream().findAny().orElse(null);
    }


    private List<Space> search(CQLQuery cql) {
        return ConfluenceClient.getInstance().search(cql)
                .asObject(SearchResults.class)
                .ifFailure(UnirestUtils::handleRequestFailure)
                .mapBody(
                        searchResults -> searchResults.getResults().stream()
                                .filter(SearchResult.SpaceSearchResult.class::isInstance)
                                .map(searchResult -> ((SearchResult.SpaceSearchResult) searchResult).getSpace())
                                .collect(Collectors.toList())
                );
    }


    private static class SingletonHolder {
        private static final ConfluenceSpaceRepository INSTANCE = new ConfluenceSpaceRepository();
    }

}
