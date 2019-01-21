package com.github.vogoltsov.vp.plugins.confluence.dialog.input;

import com.github.vogoltsov.vp.plugins.confluence.client.model.Space;
import com.github.vogoltsov.vp.plugins.confluence.dialog.ConfluenceSpaceChooserDialog;
import com.vp.plugin.ApplicationManager;
import lombok.Getter;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ConfluenceSpaceField extends JLabel {

    public static final String PROPERTY_SPACE = "space";


    @Getter
    private Space space;


    public ConfluenceSpaceField() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!ConfluenceSpaceField.this.isEnabled()) {
                    return;
                }
                ConfluenceSpaceChooserDialog dialog = new ConfluenceSpaceChooserDialog();
                ApplicationManager.instance().getViewManager().showDialog(dialog, ConfluenceSpaceField.this);
                if (dialog.isCancelled()) {
                    return;
                }
                ConfluenceSpaceField.this.setSpace(dialog.getSelectedItem());
            }
        });
        this.setEnabled(true);
        this.setText("< space not selected >");
    }


    public void setSpace(Space space) {
        Space oldValue = this.space;
        this.space = space;
        if (space != null) {
            this.setText(space.getName());
        } else {
            this.setText("< space not selected >");
        }
        this.firePropertyChange(PROPERTY_SPACE, oldValue, this.space);
    }


    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            this.setForeground(Color.BLUE.darker());
            this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            this.setForeground(Color.gray);
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

}
