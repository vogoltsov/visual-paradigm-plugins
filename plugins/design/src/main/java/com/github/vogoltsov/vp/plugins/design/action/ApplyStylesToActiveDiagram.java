package com.github.vogoltsov.vp.plugins.design.action;

import com.github.vogoltsov.vp.plugins.design.rule.DefaultDesignRuleSet;
import com.github.vogoltsov.vp.plugins.design.rule.DesignRuleSet;
import com.github.vogoltsov.vp.plugins.design.rule.DiagramElementTree;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ApplyStylesToActiveDiagram implements VPActionController {

    @Override
    public void performAction(VPAction vpAction) {
        IDiagramUIModel diagram = ApplicationManager.instance().getDiagramManager().getActiveDiagram();
        DiagramElementTree tree = new DiagramElementTree(diagram);
        DesignRuleSet designRuleSet = new DefaultDesignRuleSet();
        designRuleSet.apply(tree);
    }

    @Override
    public void update(VPAction vpAction) {
    }

}
