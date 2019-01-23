package com.github.vogoltsov.vp.plugins.common.swing;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Objects;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ButtonsPanel extends JPanel {

    public ButtonsPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        this.add(Box.createHorizontalGlue());
    }


    @SuppressWarnings("UnusedReturnValue")
    public JButton addCancelButton(ActionListener actionListener) {
        return addButton("Cancel", actionListener);
    }

    @SuppressWarnings("UnusedReturnValue")
    public JButton addButton(String title, ActionListener actionListener) {
        Objects.requireNonNull(title);
        Objects.requireNonNull(actionListener);
        // create and add a button
        JButton button = new JButton(title);
        button.addActionListener(actionListener);
        return this.addButton(button);
    }

    private JButton addButton(JButton button) {
        Objects.requireNonNull(button);
        this.add(Box.createRigidArea(new Dimension(10, 0)));
        this.add(button, BorderLayout.EAST);
        return button;
    }

}
