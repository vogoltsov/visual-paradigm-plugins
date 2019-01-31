package com.github.vogoltsov.vp.plugins.confluence.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.vogoltsov.vp.plugins.confluence.client.dto.CreateAttachmentResponse;
import com.github.vogoltsov.vp.plugins.confluence.client.dto.ListAttachmentsResponse;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Attachment;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ConfluenceAttachmentRepository {

    public static ConfluenceAttachmentRepository getInstance() {
        return SingletonHolder.INSTANCE;
    }


    /**
     * Searches attachments by content id and text.
     */
    public List<Attachment> search(String pageId, String text) {
        String lowerCaseText = text.toLowerCase();
        return findAllAttachments(pageId)
                .stream()
                .filter(attachment -> attachment.getTitle().toLowerCase().contains(lowerCaseText))
                .sorted(Comparator.comparing(Attachment::getTitle))
                .collect(Collectors.toList());
    }

    /**
     * Returns page by id, if any.
     */
    public Attachment findByPageIdAndAttachmentId(String pageId, String attachmentId) {
        if (pageId == null || attachmentId == null) {
            return null;
        }
        return findAllAttachments(pageId)
                .stream()
                .filter(attachment -> Objects.equals(attachment.getId(), attachmentId))
                .findAny()
                .orElse(null);
    }


    /**
     * Returns all attachments for content.
     * This method has a soft limit of {@code 100} attachments returned.
     */
    private List<Attachment> findAllAttachments(String pageId) {
        if (pageId == null) {
            return Collections.emptyList();
        }
        return ConfluenceClient.getInstance().get("/rest/api/content/{pageId}/child/attachment")
                .routeParam("pageId", pageId)
                .queryString("expand", "container,container.space")
                .queryString("limit", "100")
                .asObject(JsonNode.class)
                .ifFailure(ConfluenceClient.getInstance()::handleFailureResponse)
                .mapBody(ConfluenceClient.getInstance().map(ListAttachmentsResponse.class).andThen(
                        ListAttachmentsResponse::getResults
                ));
    }


    /**
     * Creates a new attachment for the given content.
     */
    public Attachment create(String pageId, String title, byte[] data) {
        return ConfluenceClient.getInstance().post("/rest/api/content/{pageId}/child/attachment")
                .routeParam("pageId", pageId)
                .field("file", new ByteArrayInputStream(data), title)
                .asObject(JsonNode.class)
                .ifFailure(ConfluenceClient.getInstance()::handleFailureResponse)
                .mapBody(ConfluenceClient.getInstance().map(CreateAttachmentResponse.class).andThen(
                        createAttachmentResponse -> createAttachmentResponse.getResults().get(0)
                ));
    }

    /**
     * Updates existing attachment's data.
     */
    public Attachment update(String pageId, String attachmentId, String title, byte[] data) {
        return ConfluenceClient.getInstance().post("/rest/api/content/{pageId}/child/attachment/{attachmentId}/data")
                .routeParam("pageId", pageId)
                .routeParam("attachmentId", attachmentId)
                .field("file", new ByteArrayInputStream(data), title)
                .asObject(JsonNode.class)
                .ifFailure(ConfluenceClient.getInstance()::handleFailureResponse)
                .mapBody(ConfluenceClient.getInstance().map(Attachment.class));
    }


    private static class SingletonHolder {
        private static final ConfluenceAttachmentRepository INSTANCE = new ConfluenceAttachmentRepository();
    }

}
