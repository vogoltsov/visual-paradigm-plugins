package com.github.vogoltsov.vp.plugins.common.swing;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Objects;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class HelpPanel extends JPanel {

    public HelpPanel(String text) {
        this(null, text);
    }

    public HelpPanel(String title, String text) {
        Objects.requireNonNull(text);
        // create border
        Border border = BorderFactory.createLineBorder(Color.gray);
        if (title != null) {
            border = BorderFactory.createTitledBorder(border, title, TitledBorder.LEFT, TitledBorder.TOP);
        }
        this.setBorder(border);
        // set layout
        this.setLayout(new BorderLayout());
        // add label
        this.add(new JLabel(text), BorderLayout.WEST);
    }

}
