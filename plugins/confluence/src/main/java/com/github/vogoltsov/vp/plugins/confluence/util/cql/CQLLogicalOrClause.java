package com.github.vogoltsov.vp.plugins.confluence.util.cql;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@RequiredArgsConstructor
class CQLLogicalOrClause extends CQLClause {
    @NonNull
    private final CQLClause[] clauses;

    @Override
    public CQLClause or(CQLClause query) {
        CQLClause[] clauses;
        if (query instanceof CQLLogicalOrClause) {
            CQLLogicalOrClause or = (CQLLogicalOrClause) query;
            clauses = Arrays.copyOf(this.clauses, this.clauses.length + or.clauses.length);
            System.arraycopy(((CQLLogicalOrClause) query).clauses, 0, clauses, this.clauses.length, or.clauses.length);
        } else {
            clauses = Arrays.copyOf(this.clauses, this.clauses.length + 1);
            clauses[clauses.length - 1] = query;
        }
        return new CQLLogicalAndClause(clauses);
    }

    @Override
    public String format() {
        return Arrays.stream(clauses)
                .map(clause -> "( " + clause.format() + " )")
                .collect(Collectors.joining(" OR "));
    }
}
