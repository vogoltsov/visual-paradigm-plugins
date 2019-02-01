package com.github.vogoltsov.vp.plugins.confluence.dialog;

import com.github.vogoltsov.vp.plugins.common.swing.ListTableModel;
import com.github.vogoltsov.vp.plugins.confluence.client.ConfluencePageRepository;
import com.github.vogoltsov.vp.plugins.confluence.client.dto.DataPage;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Page;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Space;
import com.github.vogoltsov.vp.plugins.confluence.util.swing.SearchChooserDialog;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@RequiredArgsConstructor
public class ConfluencePageChooserDialog extends SearchChooserDialog<Page> {

    private final Space space;


    @Override
    protected String getTitle() {
        return "Find Confluence Page";
    }


    @Override
    protected ListTableModel<Page> getResultsDataModel() {
        //noinspection Duplicates
        return new ListTableModel<>(
                Collections.singletonList("Title"),
                (page, column) -> {
                    switch (column) {
                        case 0:
                            return page.getTitle();
                    }
                    throw new ArrayIndexOutOfBoundsException(column);
                }
        );
    }


    @Override
    protected DataPage<Page> doSearch(String text) {
        return ConfluencePageRepository.getInstance().findBySpaceKeyAndText(
                this.space != null ? this.space.getKey() : null,
                text
        );
    }

}
