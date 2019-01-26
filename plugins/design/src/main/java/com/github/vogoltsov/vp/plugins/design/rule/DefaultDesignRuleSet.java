package com.github.vogoltsov.vp.plugins.design.rule;

import java.util.Arrays;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class DefaultDesignRuleSet extends DesignRuleSet {

    public DefaultDesignRuleSet() {
        super(Arrays.asList(
                ApplyStyleDesignRule.of(node -> node.hasStereotype("external"), "UML Component - Generic - External Component"),
                ApplyStyleDesignRule.of(DiagramElementTree.Node::hasNestedShapes, "UML Component - Generic - Parent Component"),
                ApplyStyleDesignRule.of(node -> node.hasStereotype("volume"), "UML Component - Data Volume"),
                ApplyStyleDesignRule.of(node -> node.hasStereotype("database"), "UML Component - Database Server"),
                ApplyStyleDesignRule.of(node -> node.hasStereotype("container"), "UML Component - Application Container")
        ));
    }

}
