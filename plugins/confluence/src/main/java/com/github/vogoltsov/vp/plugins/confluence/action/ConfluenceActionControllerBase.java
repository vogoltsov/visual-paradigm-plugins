package com.github.vogoltsov.vp.plugins.confluence.action;

import com.github.vogoltsov.vp.plugins.confluence.client.ConfluenceClient;
import com.github.vogoltsov.vp.plugins.confluence.dialog.ConfluenceServerConnectionDialog;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
abstract class ConfluenceActionControllerBase implements VPActionController {

    boolean checkClientNotConfigured() {
        // check if connection to confluence server is configured
        if (ConfluenceClient.getInstance().isConfigured()) {
            return false;
        }
        // show confluence configuration dialog
        ConfluenceServerConnectionDialog dialog = new ConfluenceServerConnectionDialog();
        ApplicationManager.instance().getViewManager().showDialog(
                dialog,
                ApplicationManager.instance().getViewManager().getRootFrame()
        );
        return dialog.isCancelled();
    }

    @Override
    public void update(VPAction vpAction) {
    }

}
