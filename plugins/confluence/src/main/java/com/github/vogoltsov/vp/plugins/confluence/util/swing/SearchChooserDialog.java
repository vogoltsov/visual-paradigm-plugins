package com.github.vogoltsov.vp.plugins.confluence.util.swing;

import com.github.vogoltsov.vp.plugins.common.swing.ABaseDialog;
import com.github.vogoltsov.vp.plugins.common.swing.ButtonsPanel;
import com.github.vogoltsov.vp.plugins.common.swing.ListTableModel;
import com.github.vogoltsov.vp.plugins.common.util.ExceptionUtils;
import com.github.vogoltsov.vp.plugins.confluence.client.dto.DataPage;
import com.vp.plugin.ApplicationManager;
import lombok.Getter;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import static java.awt.GridBagConstraints.BOTH;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public abstract class SearchChooserDialog<R> extends ABaseDialog {

    private JTextField searchTextField;

    private JTable resultsTable;
    private ListTableModel<R> resultsDataModel;

    private JLabel totalRowsLabel;

    private JButton chooseButton;

    @Getter
    private R selectedItem;


    @Override
    protected JPanel createContentsPanel() {
        JPanel contentsPanel = new JPanel();
        contentsPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        // new row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = BOTH;
        {
            // label
            gbc.gridx++;
            contentsPanel.add(new JLabel("Search text:"), gbc);
            // input
            gbc.gridx++;
            this.searchTextField = new JTextField(20);
            this.searchTextField.addActionListener(e -> search());
            contentsPanel.add(this.searchTextField, gbc);
        }
        // new row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = BOTH;
        {
            gbc.gridx++;
            gbc.gridwidth = 2;
            this.resultsDataModel = getResultsDataModel();
            this.resultsTable = new JTable(this.resultsDataModel);
            this.resultsTable.getSelectionModel().addListSelectionListener(
                    e -> this.chooseButton.setEnabled(this.resultsTable.getSelectedRow() >= 0)
            );
            // настроить выбор по нажатию 'enter'
            KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
            this.resultsTable.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, "choose");
            this.resultsTable.getActionMap().put("choose", new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    choose();
                }
            });
            contentsPanel.add(new JScrollPane(this.resultsTable), gbc);
        }
        // new row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = BOTH;
        {
            gbc.gridx++;
            gbc.gridwidth = 2;
            this.totalRowsLabel = new JLabel();
            contentsPanel.add(this.totalRowsLabel, gbc);
        }

        return contentsPanel;
    }

    @Override
    protected ButtonsPanel createButtonsPanel() {
        ButtonsPanel buttonsPanel = new ButtonsPanel();
        this.chooseButton = buttonsPanel.addButton("Choose", e -> choose());
        this.chooseButton.setEnabled(false);
        buttonsPanel.addCancelButton(e -> cancel());
        return buttonsPanel;
    }

    protected abstract ListTableModel<R> getResultsDataModel();

    @Override
    protected void load() {
        // pre-load data on open
        search();
    }

    private void search() {
        try {
            DataPage<R> results = doSearch(this.searchTextField.getText());
            this.resultsDataModel.setRows(results.getResults());

            String totalRowsLabelText = results.getTotalSize() + " items found";
            if (results.getSize() < results.getTotalSize()) {
                totalRowsLabelText += " (showing first " + results.getSize() + ")";
            }
            totalRowsLabelText += ".";
            this.totalRowsLabel.setText(totalRowsLabelText);
        } catch (Exception e) {
            ApplicationManager.instance().getViewManager().showMessage(ExceptionUtils.getStackTraceAsString(e));
            ApplicationManager.instance().getViewManager().showMessageDialog(
                    this,
                    "Unexpected exception while running server search: " + e.getMessage(),
                    "Server search failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    protected abstract DataPage<R> doSearch(String text);

    private void choose() {
        this.selectedItem = this.resultsDataModel.getRowAt(this.resultsTable.getSelectedRow());
        this.close();
    }


}
