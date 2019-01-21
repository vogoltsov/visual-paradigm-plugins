package com.github.vogoltsov.vp.plugins.confluence.util.cql;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@RequiredArgsConstructor
class CQLOrderBy {

    @NonNull
    private final String field;
    @NonNull
    private final CQLSortOrder sortOrder;


    public String format() {
        return field + " " + sortOrder.getCqlConstant();
    }

}
