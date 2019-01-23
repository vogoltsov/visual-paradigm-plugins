package com.github.vogoltsov.vp.plugins.confluence.client.dto;

import com.github.vogoltsov.vp.plugins.confluence.client.model.Attachment;
import lombok.Data;

import java.util.List;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@Data
public class CreateAttachmentResponse {

    private List<Attachment> results;

}
