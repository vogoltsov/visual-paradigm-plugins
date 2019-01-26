package com.github.vogoltsov.vp.plugins.design.rule;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.IStyle;
import com.vp.plugin.model.IStyleSetContainer;
import com.vp.plugin.model.factory.IModelElementFactory;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class DesignRuleSet {

    @NonNull
    private final List<DesignRule> rules;


    public DesignRuleSet(List<DesignRule> rules) {
        Objects.requireNonNull(rules);
        this.rules = Collections.unmodifiableList(new ArrayList<>(rules));
    }


    /**
     * Apply design rule set to a whole tree.
     */
    public void apply(DiagramElementTree tree) {
        Context context = new Context();
        tree.getRootNodes().forEach(node -> this.apply(context, node));
    }

    /**
     * Apply design rule set to a specified node and all its children recursively.
     */
    public void apply(DiagramElementTree.Node node) {
        Context context = new Context();
        this.apply(context, node);
    }

    private void apply(Context context, DiagramElementTree.Node node) {
        for (DesignRule rule : rules) {
            if (rule.apply(context, node)) {
                break;
            }
        }
        node.getChildren().forEach(this::apply);
    }


    /**
     * Visual Paradigm does not provide an easy way to get certain model elements (e.g. style by name).
     * Furthermore, searching for styles, stereotypes, etc. in each design rule can be resource intensive
     * as Visual Paradigm always returns a new object instance.
     * Instances of this context object act as a temporary cache for these kinds of operations.
     */
    public class Context {

        private Map<String, IStyle> styleMap;


        public Map<String, IStyle> getStyleMap() {
            if (this.styleMap == null) {
                IProject project = ApplicationManager.instance().getProjectManager().getProject();
                IStyleSetContainer styles = (IStyleSetContainer) project.toModelElementArray(IModelElementFactory.MODEL_TYPE_STYLE_SET_CONTAINER)[0];
                this.styleMap = Arrays.stream(styles.toStyleArray()).collect(Collectors.toMap(IStyle::getName, Function.identity()));
            }
            return this.styleMap;
        }

        public IStyle findStyleById(String styleId) {
            return getStyleMap().get(styleId);
        }

    }

}
