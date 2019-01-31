package com.github.vogoltsov.vp.plugins.confluence.util;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class RemoteParseException extends RemoteException {

    public RemoteParseException(Throwable cause) {
        super("Could not parse response from remote API");
    }

}
