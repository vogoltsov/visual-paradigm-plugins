package com.github.vogoltsov.vp.plugins.confluence.util.cql;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Utility class to compose CQL queries.
 *
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class CQL {

    public static CQLClause eq(String field, String value) {
        return new CQLBinaryOperatorClause(field, value, "=");
    }

    public static CQLClause like(String field, String value) {
        return new CQLBinaryOperatorClause(field, value, "~");
    }

    public static CQLClause and(CQLClause... clauses) {
        return new CQLLogicalAndClause(clauses);
    }

    public static CQLClause or(CQLClause... clauses) {
        return new CQLLogicalOrClause(clauses);
    }


    public static CQLQuery query(CQLClause where) {
        return new CQLQuery(where);
    }


}
