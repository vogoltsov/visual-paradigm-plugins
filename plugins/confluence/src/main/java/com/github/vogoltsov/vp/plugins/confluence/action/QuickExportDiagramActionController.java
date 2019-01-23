package com.github.vogoltsov.vp.plugins.confluence.action;

import com.github.vogoltsov.vp.plugins.confluence.dialog.ExportDiagramToConfluenceDialog;
import com.github.vogoltsov.vp.plugins.confluence.util.ExceptionUtils;
import com.github.vogoltsov.vp.plugins.confluence.util.vp.DiagramExportUtils;
import com.github.vogoltsov.vp.plugins.confluence.util.vp.DiagramExtendedPropertyUtils;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.diagram.IDiagramUIModel;

import javax.swing.JOptionPane;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class QuickExportDiagramActionController extends ConfluenceActionControllerBase {

    @Override
    public void performAction(VPAction vpAction) {
        // require confluence client to be configured
        if (checkClientNotConfigured()) {
            return;
        }
        // get active diagram
        IDiagramUIModel diagram = ApplicationManager.instance().getDiagramManager().getActiveDiagram();
        // get diagram export settings
        String pageId = DiagramExtendedPropertyUtils.getDiagramConfluencePageId(diagram);
        String attachmentId = DiagramExtendedPropertyUtils.getDiagramConfluenceAttachmentId(diagram);
        if (pageId != null) {
            try {
                DiagramExportUtils.export(diagram, pageId, attachmentId);
            } catch (Exception e) {
                ApplicationManager.instance().getViewManager().showMessage(ExceptionUtils.getStackTraceAsString(e));
                ApplicationManager.instance().getViewManager().showMessageDialog(
                        ApplicationManager.instance().getViewManager().getRootFrame(),
                        e.getMessage(),
                        "Couldn't export diagram to Confluence",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } else {
            // user 'export as' dialog
            ExportDiagramToConfluenceDialog dialog = new ExportDiagramToConfluenceDialog(diagram);
            ApplicationManager.instance().getViewManager().showDialog(
                    dialog,
                    ApplicationManager.instance().getViewManager().getRootFrame()
            );
        }
    }

}
