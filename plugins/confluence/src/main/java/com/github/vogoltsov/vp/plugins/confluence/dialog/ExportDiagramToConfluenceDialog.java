package com.github.vogoltsov.vp.plugins.confluence.dialog;

import com.github.vogoltsov.vp.plugins.common.swing.ABaseDialog;
import com.github.vogoltsov.vp.plugins.common.swing.AutoCompleteComboBox;
import com.github.vogoltsov.vp.plugins.common.swing.ButtonsPanel;
import com.github.vogoltsov.vp.plugins.common.swing.HelpPanel;
import com.github.vogoltsov.vp.plugins.common.util.ExceptionUtils;
import com.github.vogoltsov.vp.plugins.confluence.client.ConfluenceAttachmentRepository;
import com.github.vogoltsov.vp.plugins.confluence.client.ConfluencePageRepository;
import com.github.vogoltsov.vp.plugins.confluence.client.ConfluenceSpaceRepository;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Attachment;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Page;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Space;
import com.github.vogoltsov.vp.plugins.confluence.util.vp.DiagramExportUtils;
import com.github.vogoltsov.vp.plugins.confluence.util.vp.ProjectUtils;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Optional;

import static java.awt.GridBagConstraints.BOTH;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
@RequiredArgsConstructor
public class ExportDiagramToConfluenceDialog extends ABaseDialog {

    @NonNull
    private final IDiagramUIModel diagram;

    private AutoCompleteComboBox<Space> confluenceSpaceField;
    private AutoCompleteComboBox<Page> confluencePageField;
    private AutoCompleteComboBox<Attachment> confluenceAttachmentField;

    private JCheckBox saveExportSettingsCheckbox;

    private JButton exportButton;


    @Override
    protected String getTitle() {
        return "Export diagram to Confluence";
    }

    @Override
    protected HelpPanel createHelpPanel() {
        return new HelpPanel(
                getTitle(),
                "Attach diagram as an image to a Confluence page."
        );
    }

    @Override
    protected JPanel createContentsPanel() {
        JPanel contentsPanel = new JPanel();

        contentsPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();


        // new row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = BOTH;
        {
            // label
            gbc.gridx++;
            contentsPanel.add(new JLabel("Target space:"), gbc);
            // input
            gbc.gridx++;
            this.confluenceSpaceField = new AutoCompleteComboBox<>(
                    text -> ConfluenceSpaceRepository.getInstance().search(text).getResults(),
                    Space::getName
            );
            this.confluenceSpaceField.addItemListener(e -> {
                Space space = this.confluenceSpaceField.getSelectedItem();
                this.confluencePageField.setSelectedItem(null);
                this.confluencePageField.setEnabled(space != null);
                if (this.confluencePageField.isEnabled()) {
                    this.confluencePageField.requestFocusInWindow();
                }
            });
            this.confluenceSpaceField.setPrototypeDisplayValue(getConfluenceSpaceFieldPrototypeDisplayValue());
            this.confluenceSpaceField.setMaximumSize(this.confluenceSpaceField.getPreferredSize());
            contentsPanel.add(this.confluenceSpaceField, gbc);
        }
        // new row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = BOTH;
        {
            // label
            gbc.gridx++;
            contentsPanel.add(new JLabel("Target page:"), gbc);
            // input
            gbc.gridx++;
            this.confluencePageField = new AutoCompleteComboBox<>(
                    text -> ConfluencePageRepository.getInstance().findBySpaceKeyAndText(
                            Optional.ofNullable(this.confluenceSpaceField.getSelectedItem()).map(Space::getKey).orElse(null),
                            text
                    ).getResults(),
                    Page::getTitle
            );
            this.confluencePageField.addItemListener(e -> {
                Page page = this.confluencePageField.getSelectedItem();
                this.confluenceAttachmentField.setSelectedItem(null);
                this.confluenceAttachmentField.setEnabled(page != null);
                if (confluenceAttachmentField.isEnabled()) {
                    this.confluenceAttachmentField.requestFocusInWindow();
                }
                this.exportButton.setEnabled(this.confluenceAttachmentField.isEnabled());
            });
            this.confluencePageField.setEnabled(false);
            this.confluencePageField.setMinimumSize(new Dimension(640, 0));
            contentsPanel.add(this.confluencePageField, gbc);
        }
        // new row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = BOTH;
        {
            // label
            gbc.gridx++;
            contentsPanel.add(new JLabel("Attachment:"), gbc);
            // input
            gbc.gridx++;
            this.confluenceAttachmentField = new AutoCompleteComboBox<>(
                    text -> ConfluenceAttachmentRepository.getInstance().search(
                            Optional.ofNullable(this.confluencePageField.getSelectedItem()).map(Page::getId).orElse(null),
                            text
                    ).getResults(),
                    Attachment::getTitle
            );
            this.confluenceAttachmentField.setEmptyItemAllowed(true);
            this.confluenceAttachmentField.setEmptyItemLabel("< Export as new attachment >");
            this.confluenceAttachmentField.addItemListener(e -> this.exportButton.requestFocusInWindow());
            this.confluenceAttachmentField.setEnabled(false);
            this.confluenceAttachmentField.setMinimumSize(new Dimension(640, 0));
            contentsPanel.add(this.confluenceAttachmentField, gbc);
        }
        // new row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = BOTH;
        {
            gbc.gridx++;
            gbc.gridwidth = 2;
            contentsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
        }
        // new row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = BOTH;
        {
            // label
            gbc.gridx++;
            contentsPanel.add(new JLabel("Save export settings"), gbc);
            // input
            gbc.gridx++;
            this.saveExportSettingsCheckbox = new JCheckBox();
            this.saveExportSettingsCheckbox.setSelected(true);
            contentsPanel.add(this.saveExportSettingsCheckbox, gbc);
        }

        return contentsPanel;
    }

    private Space getConfluenceSpaceFieldPrototypeDisplayValue() {
        Space confluenceSpaceFieldPrototypeDisplayValue = new Space();
        confluenceSpaceFieldPrototypeDisplayValue.setKey(new String(new char[10]).replace('\0', 'X'));
        confluenceSpaceFieldPrototypeDisplayValue.setName(new String(new char[80]).replace('\0', 'x'));
        return confluenceSpaceFieldPrototypeDisplayValue;
    }

    @Override
    protected ButtonsPanel createButtonsPanel() {
        ButtonsPanel buttonsPanel = new ButtonsPanel();
        this.exportButton = buttonsPanel.addButton("Export", e -> export());
        this.exportButton.setEnabled(false);
        buttonsPanel.addCancelButton(e -> cancel());
        return buttonsPanel;
    }

    @Override
    protected void load() {
        String spaceKey = ProjectUtils.getConfluenceSpaceKey();
        String pageId = DiagramExportUtils.getDiagramConfluencePageId(diagram);
        String attachmentId = DiagramExportUtils.getDiagramConfluenceAttachmentId(diagram);

        // load data from confluence
        Space space = null;
        Page page = null;
        Attachment attachment = null;
        if (pageId != null) {
            if (attachmentId != null) {
                attachment = ConfluenceAttachmentRepository.getInstance().findByPageIdAndAttachmentId(pageId, attachmentId);
            }
            if (attachment != null) {
                page = attachment.getPage();
            } else {
                page = ConfluencePageRepository.getInstance().findById(pageId);
            }
        }
        if (page != null) {
            space = page.getSpace();
        } else if (spaceKey != null) {
            space = ConfluenceSpaceRepository.getInstance().findByKey(spaceKey);
        }

        this.confluenceSpaceField.setSelectedItem(space);
        this.confluencePageField.setSelectedItem(page);
        this.confluenceAttachmentField.setSelectedItem(attachment);
    }

    @Override
    public void shown() {
        if (this.confluenceSpaceField.getSelectedItem() == null) {
            this.confluenceSpaceField.requestFocusInWindow();
        } else if (this.confluencePageField.getSelectedItem() == null) {
            this.confluencePageField.requestFocusInWindow();
        } else {
            this.exportButton.requestFocusInWindow();
        }
    }

    private void export() {
        Attachment attachment = this.confluenceAttachmentField.getSelectedItem();
        Page page = Optional.ofNullable(attachment).map(Attachment::getPage).orElseGet(this.confluencePageField::getSelectedItem);
        if (page == null) {
            throw new RuntimeException("Page cannot be null");
        }
        try {
            attachment = DiagramExportUtils.export(diagram, page.getId(), attachment != null ? attachment.getId() : null);
        } catch (Exception e) {
            ApplicationManager.instance().getViewManager().showMessage(ExceptionUtils.getStackTraceAsString(e));
            ApplicationManager.instance().getViewManager().showMessageDialog(
                    this,
                    e.getMessage(),
                    "Couldn't export diagram to Confluence",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        // save export settings to diagram extended properties if needed
        if (this.saveExportSettingsCheckbox.isSelected()) {
            DiagramExportUtils.setDiagramConfluencePageId(diagram, page.getId());
            DiagramExportUtils.setDiagramConfluenceAttachmentId(diagram, attachment.getId());
        }
        // show success dialog
        ApplicationManager.instance().getViewManager().showMessageDialog(
                this,
                "Diagram successfully exported as " + attachment.getTitle(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );
        this.close();
    }


}
