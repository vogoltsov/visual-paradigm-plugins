package com.github.vogoltsov.vp.plugins.confluence.dialog;

import com.github.vogoltsov.vp.plugins.confluence.client.ConfluenceSpaceRepository;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Space;
import com.github.vogoltsov.vp.plugins.common.swing.HelpPanel;
import com.github.vogoltsov.vp.plugins.common.swing.ListTableModel;
import com.github.vogoltsov.vp.plugins.confluence.util.swing.SearchChooserDialog;

import java.util.Arrays;
import java.util.List;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ConfluenceSpaceChooserDialog extends SearchChooserDialog<Space> {

    @Override
    protected String getTitle() {
        return "Find Confluence Space";
    }


    @Override
    protected HelpPanel createHelpPanel() {
        return new HelpPanel(
                "",
                "Confluence spaces are created on a per-project basis." +
                        " Search by space key or name."
        );
    }

    @Override
    protected ListTableModel<Space> getResultsDataModel() {
        return new ListTableModel<>(
                Arrays.asList("Key", "Name"),
                (space, column) -> {
                    switch (column) {
                        case 0:
                            return space.getKey();
                        case 1:
                            return space.getName();
                    }
                    throw new ArrayIndexOutOfBoundsException(column);
                }
        );
    }


    @Override
    protected List<Space> doSearch(String text) {
        return ConfluenceSpaceRepository.getInstance().search(text);
    }

}
