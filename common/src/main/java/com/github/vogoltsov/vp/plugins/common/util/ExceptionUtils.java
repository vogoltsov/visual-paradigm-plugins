package com.github.vogoltsov.vp.plugins.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ExceptionUtils {

    public static String getStackTraceAsString(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

}
