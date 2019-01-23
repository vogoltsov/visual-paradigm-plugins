package com.github.vogoltsov.vp.plugins.confluence.util.swing;

import com.vp.plugin.view.IDialog;
import com.vp.plugin.view.IDialogHandler;
import lombok.AccessLevel;
import lombok.Getter;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Optional;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public abstract class ABaseDialog extends JPanel implements IDialogHandler {

    @Getter(AccessLevel.PROTECTED)
    private IDialog dialog;
    @Getter
    private boolean cancelled = false;


    @SuppressWarnings("WeakerAccess")
    protected boolean isModal() {
        return true;
    }

    /**
     * Returns title to be used for this dialog.
     */
    protected abstract String getTitle();

    /**
     * Initializes swing components.
     */
    private void init() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        Optional.ofNullable(createHelpPanel()).ifPresent(this::add);
        Optional.ofNullable(createContentsPanel()).ifPresent(this::add);
        Optional.ofNullable(createButtonsPanel()).ifPresent(this::add);
        this.registerEscapeAction();
    }

    private void registerEscapeAction() {
        String actionKey = getClass().getName() + ":cancel";
        // register escape key action
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                actionKey
        );
        rootPane.getActionMap().put(actionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ABaseDialog.this.canClosed()) {
                    ABaseDialog.this.cancel();
                }
            }
        });
    }

    /**
     * Creates and returns help panel for this dialog.
     */
    protected HelpPanel createHelpPanel() {
        return null;
    }

    /**
     * Creates and returns contents panel for this dialog.
     */
    protected abstract JPanel createContentsPanel();

    /**
     * Creates and returns buttons panel for this dialog.
     */
    protected abstract ButtonsPanel createButtonsPanel();

    /**
     * Loads data into swing components and initializes their state.
     */
    protected abstract void load();

    /**
     * @see JDialog#pack()
     */
    protected void pack() {
        this.dialog.pack();
    }

    /**
     * Closes this dialog.
     */
    @SuppressWarnings("WeakerAccess")
    protected void close() {
        this.dialog.close();
    }

    /**
     * Closes this dialog and marks as cancelled.
     */
    @SuppressWarnings("WeakerAccess")
    protected void cancel() {
        this.dialog.close();
        this.cancelled = true;
    }


    @Override
    public Component getComponent() {
        return this;
    }

    @Override
    public void prepare(IDialog iDialog) {
        this.dialog = iDialog;

        this.dialog.setTitle(getTitle());
        this.dialog.setModal(isModal());

        this.init();
        this.load();
        this.pack();
    }

    @Override
    public void shown() {
    }

    @Override
    public boolean canClosed() {
        return true;
    }

}
