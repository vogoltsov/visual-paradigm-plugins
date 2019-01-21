package com.github.vogoltsov.vp.plugins.confluence.action;

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
public class QuickExportDiagramActionController extends ExportDiagramAsActionController {

    @Override
    public void performAction(VPAction vpAction) {
        IDiagramUIModel diagram = ApplicationManager.instance().getDiagramManager().getActiveDiagram();
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
            super.performAction(vpAction);
        }
    }

}
