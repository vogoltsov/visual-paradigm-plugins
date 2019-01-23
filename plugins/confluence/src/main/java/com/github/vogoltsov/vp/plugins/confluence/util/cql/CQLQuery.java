package com.github.vogoltsov.vp.plugins.confluence.util.cql;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@RequiredArgsConstructor
public class CQLQuery {

    @NonNull
    private CQLClause where;

    private final List<CQLOrderBy> orderBy = new LinkedList<>();
    private final List<String> expansionProperties = new LinkedList<>();


    /**
     * Adds an {@code AND} clause to this query.
     */
    public CQLQuery and(CQLClause clause) {
        this.where = this.where.and(clause);
        return this;
    }

    /**
     * Adds an {@code OR} clause to this query.
     */
    public CQLQuery or(CQLClause clause) {
        this.where = this.where.or(clause);
        return this;
    }

    /**
     * Adds {@code ORDER BY} clause to this query.
     */
    public CQLQuery orderBy(String field) {
        return orderBy(field, CQLSortOrder.ASCENDING);
    }

    /**
     * Adds {@code ORDER BY} clause to this query.
     */
    public CQLQuery orderBy(String field, CQLSortOrder sortOrder) {
        this.orderBy.add(new CQLOrderBy(field, sortOrder));
        return this;
    }

    /**
     * Adds field expansion to the query.
     */
    public CQLQuery expand(String field) {
        this.expansionProperties.add(field);
        return this;
    }


    /**
     * Returns formatted query string to be used in Confluence API.
     */
    public String getQueryString() {
        String queryString = where.format();
        if (!orderBy.isEmpty()) {
            queryString += " ORDER BY " + orderBy.stream().map(CQLOrderBy::format).collect(Collectors.joining(", "));
        }
        return queryString;
    }

    /**
     * Returns a {@link Collection} of properties to be expanded in query results.
     */
    public Collection<String> getExpandedProperties() {
        return Collections.unmodifiableCollection(this.expansionProperties);
    }

}
