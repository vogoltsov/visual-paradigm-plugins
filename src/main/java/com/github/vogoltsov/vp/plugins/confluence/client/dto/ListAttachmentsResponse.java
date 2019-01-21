package com.github.vogoltsov.vp.plugins.confluence.client.dto;

import com.github.vogoltsov.vp.plugins.confluence.client.model.Attachment;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class ListAttachmentsResponse extends DataPage<Attachment> {
}
