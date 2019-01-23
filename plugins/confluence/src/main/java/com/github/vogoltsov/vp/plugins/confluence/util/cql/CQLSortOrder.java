package com.github.vogoltsov.vp.plugins.confluence.util.cql;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@RequiredArgsConstructor
public enum CQLSortOrder {
    ASCENDING("ASC"),
    DESCENDING("DESC");

    @Getter
    private final String cqlConstant;
}
