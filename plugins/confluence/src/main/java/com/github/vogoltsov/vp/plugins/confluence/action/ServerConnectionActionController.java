package com.github.vogoltsov.vp.plugins.confluence.action;

import com.github.vogoltsov.vp.plugins.confluence.dialog.ConfluenceServerConnectionDialog;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ServerConnectionActionController implements VPActionController {

    @Override
    public void performAction(VPAction vpAction) {
        ApplicationManager.instance().getViewManager().showDialog(new ConfluenceServerConnectionDialog());
    }

    @Override
    public void update(VPAction vpAction) {
    }

}
