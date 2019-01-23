package com.github.vogoltsov.vp.plugins.confluence.client.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@Data
@EqualsAndHashCode(of = {"key"})
public class Space {

    private String key;
    private String name;
    private String type;

    private Map<String, String> _links;

}
