package com.github.vogoltsov.vp.plugins.confluence.util.cql;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@SuppressWarnings("unused")
public abstract class CQLClause {

    public CQLClause and(CQLClause query) {
        return CQL.and(this, query);
    }

    public CQLClause or(CQLClause query) {
        return CQL.or(this, query);
    }

    public abstract String format();

}
