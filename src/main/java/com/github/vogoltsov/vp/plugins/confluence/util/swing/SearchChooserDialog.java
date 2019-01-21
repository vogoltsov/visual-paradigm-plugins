package com.github.vogoltsov.vp.plugins.confluence.util.swing;

import com.github.vogoltsov.vp.plugins.confluence.util.ExceptionUtils;
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
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;

import static java.awt.GridBagConstraints.BOTH;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public abstract class SearchChooserDialog<R> extends ABaseDialog {

    private JTextField searchTextField;

    private JTable resultsTable;
    private ListTableModel<R> resultsDataModel;

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
            this.resultsTable = new JTable(this.resultsDataModel) {
                public boolean getScrollableTracksViewportHeight() {
                    if (getParent() instanceof JViewport)
                        return (getParent().getHeight() > getPreferredSize().height);

                    return super.getScrollableTracksViewportHeight();
                }

                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (getRowCount() == 0) {
                        Graphics2D g2d = (Graphics2D) g;
                        g2d.setColor(Color.BLACK);
                        g2d.drawString("Nothing found to display.", 10, 20);
                    }
                }
            };
            this.resultsTable.getSelectionModel().addListSelectionListener(
                    e -> {
                        int selectedRow = this.resultsTable.getSelectedRow();
                        this.chooseButton.setEnabled(selectedRow >= 0);
                    }
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
            List<R> results = doSearch(this.searchTextField.getText());
            this.resultsDataModel.setRows(results);
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

    protected abstract List<R> doSearch(String text);

    private void choose() {
        this.selectedItem = this.resultsDataModel.getRowAt(this.resultsTable.getSelectedRow());
        this.close();
    }


}
