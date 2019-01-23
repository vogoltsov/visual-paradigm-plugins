package com.github.vogoltsov.vp.plugins.confluence.action;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.VPPluginInfo;
import com.vp.plugin.action.VPAction;
import com.vp.plugin.action.VPActionController;

import java.util.Arrays;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ReloadAllPlugins implements VPActionController {

    @Override
    public void performAction(VPAction vpAction) {
        Arrays.stream(ApplicationManager.instance().getPluginInfos())
                .map(VPPluginInfo::getPluginId)
                .forEach(ApplicationManager.instance()::reloadPluginClasses);
    }

    @Override
    public void update(VPAction vpAction) {
    }

}
