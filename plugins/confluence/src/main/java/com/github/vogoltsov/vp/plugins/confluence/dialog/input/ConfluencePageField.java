package com.github.vogoltsov.vp.plugins.confluence.dialog.input;

import com.github.vogoltsov.vp.plugins.confluence.client.model.Content;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Page;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Space;
import com.github.vogoltsov.vp.plugins.confluence.dialog.ConfluencePageChooserDialog;
import com.vp.plugin.ApplicationManager;
import lombok.Getter;

import javax.swing.JLabel;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ConfluencePageField extends JLabel {

    public static final String PROPERTY_PAGE = "page";


    @Getter
    private Space space;
    @Getter
    private Page page;


    public ConfluencePageField() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!ConfluencePageField.this.isEnabled()) {
                    return;
                }
                ConfluencePageChooserDialog dialog = new ConfluencePageChooserDialog(space);
                ApplicationManager.instance().getViewManager().showDialog(dialog, ConfluencePageField.this);
                if (dialog.isCancelled()) {
                    return;
                }
                ConfluencePageField.this.setPage(dialog.getSelectedItem());
            }
        });
        this.setEnabled(true);
        this.setText("< page not selected >");
    }


    public void setSpace(Space space) {
        if (Objects.equals(this.space, space)) {
            return;
        }
        this.setSpaceInternal(space);
        this.setPage(null);
    }

    private void setSpaceInternal(Space space) {
        this.space = space;
        this.setEnabled(this.space != null);
    }

    public void setPage(Page page) {
        Content oldValue = this.page;
        this.page = page;
        if (page != null) {
            this.setSpaceInternal(this.page.getSpace());
            this.setText(page.getTitle());
        } else {
            this.setText("< page not selected >");
        }
        this.firePropertyChange(PROPERTY_PAGE, oldValue, this.page);
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
