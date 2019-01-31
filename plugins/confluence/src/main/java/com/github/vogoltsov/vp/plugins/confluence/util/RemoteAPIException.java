package com.github.vogoltsov.vp.plugins.confluence.util;

import lombok.Getter;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@Getter
@SuppressWarnings("WeakerAccess")
public class RemoteAPIException extends RuntimeException {

    private final int apiStatusCode;
    private final String apiMessage;
    private final String apiReason;


    public RemoteAPIException(int apiStatusCode, String apiMessage, String apiReason) {
        super("Remote API returned error code " + apiStatusCode + (apiMessage != null ? ": " + apiMessage : ""));
        this.apiStatusCode = apiStatusCode;
        this.apiMessage = apiMessage;
        this.apiReason = apiReason;
    }

}
