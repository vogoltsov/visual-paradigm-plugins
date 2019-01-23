package com.github.vogoltsov.vp.plugins.confluence.dialog;

import com.github.vogoltsov.vp.plugins.confluence.client.ConfluenceAttachmentRepository;
import com.github.vogoltsov.vp.plugins.confluence.client.ConfluencePageRepository;
import com.github.vogoltsov.vp.plugins.confluence.client.ConfluenceSpaceRepository;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Attachment;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Page;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Space;
import com.github.vogoltsov.vp.plugins.confluence.dialog.input.ConfluenceAttachmentField;
import com.github.vogoltsov.vp.plugins.confluence.dialog.input.ConfluencePageField;
import com.github.vogoltsov.vp.plugins.confluence.dialog.input.ConfluenceSpaceField;
import com.github.vogoltsov.vp.plugins.confluence.util.ExceptionUtils;
import com.github.vogoltsov.vp.plugins.confluence.util.swing.ABaseDialog;
import com.github.vogoltsov.vp.plugins.confluence.util.swing.ButtonsPanel;
import com.github.vogoltsov.vp.plugins.confluence.util.swing.HelpPanel;
import com.github.vogoltsov.vp.plugins.confluence.util.vp.DiagramExportUtils;
import com.github.vogoltsov.vp.plugins.confluence.util.vp.DiagramExtendedPropertyUtils;
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

    private ConfluenceSpaceField confluenceSpaceField;
    private ConfluencePageField confluencePageField;
    private ConfluenceAttachmentField confluenceAttachmentField;

    private JCheckBox exportDoubleResolution;
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
            this.confluenceSpaceField = new ConfluenceSpaceField();
            this.confluenceSpaceField.addPropertyChangeListener(
                    ConfluenceSpaceField.PROPERTY_SPACE,
                    evt -> {
                        this.pack();
                        this.confluencePageField.setSpace((Space) evt.getNewValue());
                    }
            );
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
            this.confluencePageField = new ConfluencePageField();
            this.confluencePageField.addPropertyChangeListener(
                    ConfluencePageField.PROPERTY_PAGE,
                    evt -> {
                        this.pack();
                        this.confluenceAttachmentField.setPage((Page) evt.getNewValue());
                        this.exportButton.setEnabled(evt.getNewValue() != null);
                    }
            );
            this.confluencePageField.setEnabled(false);
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
            this.confluenceAttachmentField = new ConfluenceAttachmentField();
            this.confluenceAttachmentField.addPropertyChangeListener(
                    ConfluenceAttachmentField.PROPERTY_ATTACHMENT,
                    evt -> this.pack()
            );
            this.confluenceAttachmentField.setEnabled(false);
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
        String pageId = DiagramExtendedPropertyUtils.getDiagramConfluencePageId(diagram);
        String attachmentId = DiagramExtendedPropertyUtils.getDiagramConfluenceAttachmentId(diagram);

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
            space = ConfluenceSpaceRepository.getInstance().findById(spaceKey);
        }

        this.confluenceSpaceField.setSpace(space);
        this.confluencePageField.setPage(page);
        this.confluenceAttachmentField.setAttachment(attachment);
    }

    private void export() {
        Attachment attachment = this.confluenceAttachmentField.getAttachment();
        Page page = Optional.ofNullable(attachment).map(Attachment::getPage).orElseGet(this.confluencePageField::getPage);
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
        // save export settings to diagram extended properteis if needed
        if (this.saveExportSettingsCheckbox.isSelected()) {
            DiagramExtendedPropertyUtils.setDiagramConfluencePageId(diagram, page.getId());
            DiagramExtendedPropertyUtils.setDiagramConfluenceAttachmentId(diagram, attachment.getId());
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
