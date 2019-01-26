package com.github.vogoltsov.vp.plugins.design.rule;

import com.vp.plugin.model.IStyle;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.function.Predicate;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@RequiredArgsConstructor(staticName = "of")
public class ApplyStyleDesignRule implements DesignRule {

    @NonNull
    private final Predicate<DiagramElementTree.Node> condition;
    @NonNull
    private final String styleId;


    @Override
    public boolean apply(DesignRuleSet.Context context, DiagramElementTree.Node node) {
        if (!Boolean.TRUE.equals(condition.test(node))) {
            return false;
        }
        IStyle style = context.findStyleById(styleId);
        if (style == null) {
            return false;
        }
        node.setStyle(style);
        return true;
    }

}
