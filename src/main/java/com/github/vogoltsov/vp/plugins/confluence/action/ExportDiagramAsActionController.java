package com.github.vogoltsov.vp.plugins.confluence.action;

import com.github.vogoltsov.vp.plugins.confluence.client.ConfluenceClient;
import com.github.vogoltsov.vp.plugins.confluence.dialog.ConfluenceServerConnectionDialog;
import com.github.vogoltsov.vp.plugins.confluence.dialog.ExportDiagramToConfluenceDialog;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.ViewManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;
import com.vp.plugin.diagram.IDiagramUIModel;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ExportDiagramAsActionController implements VPActionController {


    public void performAction(VPAction vpAction) {
        ApplicationManager applicationManager = ApplicationManager.instance();
        ViewManager viewManager = applicationManager.getViewManager();

        IDiagramUIModel diagram = applicationManager.getDiagramManager().getActiveDiagram();
        if (diagram == null) {
            ApplicationManager.instance().getViewManager().showMessage("No active diagram.");
            return;
        }

        // check if connection to confluence server is configured
        if (!ConfluenceClient.getInstance().isConfigured()) {
            ConfluenceServerConnectionDialog dialog = new ConfluenceServerConnectionDialog();
            viewManager.showDialog(dialog, viewManager.getRootFrame());
            if (dialog.isCancelled()) {
                return;
            }
        }
        // show export dialog
        ExportDiagramToConfluenceDialog dialog = new ExportDiagramToConfluenceDialog(diagram);
        ApplicationManager.instance().getViewManager().showDialog(dialog, viewManager.getRootFrame());
    }

    public void update(VPAction vpAction) {
    }

}
