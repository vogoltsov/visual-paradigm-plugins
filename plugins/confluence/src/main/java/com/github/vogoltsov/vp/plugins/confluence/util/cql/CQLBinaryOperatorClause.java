package com.github.vogoltsov.vp.plugins.confluence.util.cql;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@RequiredArgsConstructor
class CQLBinaryOperatorClause extends CQLClause {
    @NonNull
    private final String field;
    @NonNull
    private final String value;
    @NonNull
    private final String operator;

    @Override
    public String format() {
        // todo escape all
        return field + " " + operator + " '" + value + "'";
    }
}
