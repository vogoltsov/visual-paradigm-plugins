package com.github.vogoltsov.vp.plugins.confluence.action;

import com.github.vogoltsov.vp.plugins.confluence.dialog.ExportDiagramToConfluenceDialog;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.diagram.IDiagramUIModel;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ExportDiagramAsActionController extends ConfluenceActionControllerBase {


    public void performAction(VPAction vpAction) {
        // require confluence client to be configured
        if (checkClientNotConfigured()) {
            return;
        }
        // get active diagram
        IDiagramUIModel diagram = ApplicationManager.instance().getDiagramManager().getActiveDiagram();
        // show export dialog
        ExportDiagramToConfluenceDialog dialog = new ExportDiagramToConfluenceDialog(diagram);
        ApplicationManager.instance().getViewManager().showDialog(
                dialog,
                ApplicationManager.instance().getViewManager().getRootFrame()
        );
    }

}
