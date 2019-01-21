package com.github.vogoltsov.vp.plugins.confluence.client.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Content;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Space;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.ZonedDateTime;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "entityType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SearchResult.SpaceSearchResult.class, name = "space"),
        @JsonSubTypes.Type(value = SearchResult.ContentSearchResult.class, name = "content"),
})
@Data
public abstract class SearchResult {

    private String title;
    private String url;

    private ZonedDateTime lastModified;


    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class SpaceSearchResult extends SearchResult {
        private Space space;
    }


    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class ContentSearchResult extends SearchResult {
        private Content content;
    }

}
