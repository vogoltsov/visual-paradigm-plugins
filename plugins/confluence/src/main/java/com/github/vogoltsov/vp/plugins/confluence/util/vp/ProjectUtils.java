package com.github.vogoltsov.vp.plugins.confluence.util.vp;

import com.vp.plugin.ApplicationManager;
import com.vp.plugin.model.IProject;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ProjectUtils {

    public static final String PROPERTY_CONFLUENCE_URL = "confluence.url";
    public static final String PROPERTY_CONFLUENCE_SPACE_KEY = "confluence.space.key";


    public static IProject project() {
        return ApplicationManager.instance().getProjectManager().getProject();
    }

    public static String getConfluenceServerUrl() {
        return project().getProjectProperties().getProperty(PROPERTY_CONFLUENCE_URL);
    }

    public static void setConfluenceServerUrl(String confluenceServerUrl) {
        project().getProjectProperties().setProperty(PROPERTY_CONFLUENCE_URL, confluenceServerUrl);
    }

    public static String getConfluenceSpaceKey() {
        return project().getProjectProperties().getProperty(PROPERTY_CONFLUENCE_SPACE_KEY);
    }

    public static void setConfluenceSpaceKey(String spaceKey) {
        project().getProjectProperties().setProperty(PROPERTY_CONFLUENCE_SPACE_KEY, spaceKey);
    }

}
