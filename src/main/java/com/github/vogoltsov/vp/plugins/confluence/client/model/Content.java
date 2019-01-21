package com.github.vogoltsov.vp.plugins.confluence.client.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Attachment.class, name = "attachment"),
        @JsonSubTypes.Type(value = Page.class, name = "page"),
})
@Data
public abstract class Content {

    @EqualsAndHashCode.Include
    private String id;

    private String status;
    private String title;

    private Map<String, String> _links;

}
