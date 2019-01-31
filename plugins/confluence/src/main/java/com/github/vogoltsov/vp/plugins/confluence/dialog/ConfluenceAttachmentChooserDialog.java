package com.github.vogoltsov.vp.plugins.confluence.dialog;

import com.github.vogoltsov.vp.plugins.common.swing.ListTableModel;
import com.github.vogoltsov.vp.plugins.confluence.client.ConfluenceAttachmentRepository;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Attachment;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Page;
import com.github.vogoltsov.vp.plugins.confluence.util.swing.SearchChooserDialog;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@RequiredArgsConstructor
public class ConfluenceAttachmentChooserDialog extends SearchChooserDialog<Attachment> {

    private final Page page;


    @Override
    protected String getTitle() {
        return "Find Confluence Page";
    }


    @Override
    protected ListTableModel<Attachment> getResultsDataModel() {
        //noinspection Duplicates
        return new ListTableModel<>(
                Collections.singletonList("Title"),
                (attachment, column) -> {
                    switch (column) {
                        case 0:
                            return attachment.getTitle();
                    }
                    throw new ArrayIndexOutOfBoundsException(column);
                }
        );
    }


    @Override
    protected List<Attachment> doSearch(String text) {
        return ConfluenceAttachmentRepository.getInstance().search(
                this.page != null ? this.page.getId() : null,
                text
        );
    }

}
