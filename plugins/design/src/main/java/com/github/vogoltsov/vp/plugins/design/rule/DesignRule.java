package com.github.vogoltsov.vp.plugins.design.rule;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public interface DesignRule {

    /**
     * Applies current rule to a single node.
     *
     * @return {@code true} if rule has been applied and no further processing is required
     */
    boolean apply(DesignRuleSet.Context context, DiagramElementTree.Node node);

}
