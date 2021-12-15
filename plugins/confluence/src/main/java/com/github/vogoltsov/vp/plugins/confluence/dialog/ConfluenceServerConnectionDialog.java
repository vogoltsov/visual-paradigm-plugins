package com.github.vogoltsov.vp.plugins.confluence.dialog;

import com.github.vogoltsov.vp.plugins.common.swing.ABaseDialog;
import com.github.vogoltsov.vp.plugins.common.swing.ButtonsPanel;
import com.github.vogoltsov.vp.plugins.common.swing.DocumentListenerAdapter;
import com.github.vogoltsov.vp.plugins.common.swing.HelpPanel;
import com.github.vogoltsov.vp.plugins.common.util.ExceptionUtils;
import com.github.vogoltsov.vp.plugins.confluence.client.ConfluenceClient;
import com.github.vogoltsov.vp.plugins.confluence.util.vp.ProjectUtils;
import com.vp.plugin.ApplicationManager;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import static java.awt.GridBagConstraints.BOTH;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ConfluenceServerConnectionDialog extends ABaseDialog {

    private JTextField confluenceServerUrlField;
    private JCheckBox saveConfluenceServerUrlToProjectCheckbox;
    private JCheckBox ignoreCertificateErrorsCheckbox;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private JButton connectButton;


    @Override
    protected String getTitle() {
        return "Connect to Confluence";
    }

    @Override
    protected HelpPanel createHelpPanel() {
        return new HelpPanel(
                "Confluence",
                "Connect to Atlassian Confluence server to export diagrams."
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
            contentsPanel.add(new JLabel("Confluence server URL:"), gbc);
            // input
            gbc.gridx++;
            this.confluenceServerUrlField = new JTextField(25);
            this.confluenceServerUrlField.setInputVerifier(new InputVerifier() {
                @Override
                public boolean verify(JComponent input) {
                    try {
                        new URL(((JTextField) input).getText());
                        return true;
                    } catch (MalformedURLException e) {
                        return false;
                    }
                }
            });
            this.confluenceServerUrlField.getDocument().addDocumentListener(
                    (DocumentListenerAdapter) e -> this.connectButton.setEnabled(!this.confluenceServerUrlField.getText().isEmpty())
            );
            this.confluenceServerUrlField.addActionListener(e -> this.usernameField.requestFocusInWindow());
            contentsPanel.add(this.confluenceServerUrlField, gbc);
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
            contentsPanel.add(new JLabel("Save as Project default"), gbc);
            // input
            gbc.gridx++;
            this.saveConfluenceServerUrlToProjectCheckbox = new JCheckBox();
            this.saveConfluenceServerUrlToProjectCheckbox.setSelected(true);
            contentsPanel.add(this.saveConfluenceServerUrlToProjectCheckbox, gbc);
        }
        // new horizontal separator
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = BOTH;
        contentsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
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
            contentsPanel.add(new JLabel("Ignore certificate errors"), gbc);
            // input
            gbc.gridx++;
            this.ignoreCertificateErrorsCheckbox = new JCheckBox();
            this.ignoreCertificateErrorsCheckbox.setSelected(false);
            contentsPanel.add(this.ignoreCertificateErrorsCheckbox, gbc);
        }
        // new horizontal separator
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = BOTH;
        contentsPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
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
            contentsPanel.add(new JLabel("Username:"), gbc);
            // input
            gbc.gridx++;
            this.usernameField = new JTextField(10);
            this.usernameField.getDocument().addDocumentListener(
                    (DocumentListenerAdapter) e -> this.passwordField.setEnabled(!this.usernameField.getText().isEmpty())
            );
            this.usernameField.addActionListener(e -> this.passwordField.requestFocusInWindow());
            contentsPanel.add(this.usernameField, gbc);
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
            contentsPanel.add(new JLabel("Password:"), gbc);
            // input
            gbc.gridx++;
            this.passwordField = new JPasswordField(10);
            this.passwordField.addActionListener(e -> this.connectButton.doClick());
            this.passwordField.setEnabled(false);
            contentsPanel.add(this.passwordField, gbc);
        }
        return contentsPanel;
    }

    @Override
    protected ButtonsPanel createButtonsPanel() {
        ButtonsPanel buttonsPanel = new ButtonsPanel();
        this.connectButton = buttonsPanel.addButton("Connect", e -> connect());
        this.connectButton.setEnabled(false);
        buttonsPanel.addCancelButton(e -> cancel());
        return buttonsPanel;
    }

    @Override
    protected void load() {
        ConfluenceClient client = ConfluenceClient.getInstance();
        if (client.isConfigured()) {
            this.confluenceServerUrlField.setText(client.getBaseUrl());
        } else {
            Optional.ofNullable(ProjectUtils.getConfluenceServerUrl())
                    .ifPresent(this.confluenceServerUrlField::setText);
        }
        this.ignoreCertificateErrorsCheckbox.setSelected(!client.isVerifySsl());
        Optional.ofNullable(client.getUsername())
                .ifPresent(this.usernameField::setText);
        Optional.ofNullable(client.getPassword())
                .ifPresent(this.passwordField::setText);
    }

    @Override
    public void shown() {
        if (!this.confluenceServerUrlField.getText().isEmpty()) {
            this.usernameField.requestFocusInWindow();
        } else {
            this.confluenceServerUrlField.requestFocusInWindow();
        }
    }

    private void connect() {
        try {
            ConfluenceClient client = new ConfluenceClient();
            this.configure(client);
            client.testConnection();
        } catch (Exception e) {
            ApplicationManager.instance().getViewManager().showMessage(ExceptionUtils.getStackTraceAsString(e));
            int result = ApplicationManager.instance().getViewManager().showConfirmDialog(
                    this,
                    "<html><body>" +
                            "<p>Error message: " + e.getMessage() + "</p>" +
                            "<p>Do you want to proceed?</p>" +
                            "</html></body>",
                    "Couldn't connect to Confluence server",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );
            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }


        if (this.saveConfluenceServerUrlToProjectCheckbox.isSelected()) {
            ProjectUtils.setConfluenceServerUrl(confluenceServerUrlField.getText());
        }
        this.configure(ConfluenceClient.getInstance());
        this.close();
    }


    private void configure(ConfluenceClient client) {
        client.configure(this.confluenceServerUrlField.getText(), !this.ignoreCertificateErrorsCheckbox.isSelected(), this.usernameField.getText(), new String(this.passwordField.getPassword()));
    }

}
