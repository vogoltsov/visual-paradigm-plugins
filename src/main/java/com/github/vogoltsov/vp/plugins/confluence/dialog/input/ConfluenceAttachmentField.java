package com.github.vogoltsov.vp.plugins.confluence.dialog.input;

import com.github.vogoltsov.vp.plugins.confluence.client.model.Attachment;
import com.github.vogoltsov.vp.plugins.confluence.client.model.Page;
import com.github.vogoltsov.vp.plugins.confluence.dialog.ConfluenceAttachmentChooserDialog;
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
public class ConfluenceAttachmentField extends JLabel {

    public static final String PROPERTY_ATTACHMENT = "attachment";


    @Getter
    private Page page;
    @Getter
    private Attachment attachment;


    public ConfluenceAttachmentField() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!ConfluenceAttachmentField.this.isEnabled()) {
                    return;
                }
                ConfluenceAttachmentChooserDialog dialog = new ConfluenceAttachmentChooserDialog(page);
                ApplicationManager.instance().getViewManager().showDialog(dialog, ConfluenceAttachmentField.this);
                if (dialog.isCancelled()) {
                    return;
                }
                ConfluenceAttachmentField.this.setAttachment(dialog.getSelectedItem());
            }
        });
        this.setEnabled(true);
        this.setText("< attachment not selected >");
    }


    public void setPage(Page page) {
        if (Objects.equals(this.page, page)) {
            return;
        }
        this.setPageInternal(page);
        this.setAttachment(null);
    }

    private void setPageInternal(Page page) {
        this.page = page;
        this.setEnabled(this.page != null);
    }

    public void setAttachment(Attachment attachment) {
        if (Objects.equals(this.attachment, attachment)) {
            return;
        }
        Attachment oldValue = this.attachment;
        this.attachment = attachment;
        if (this.attachment != null) {
            this.setPageInternal(this.attachment.getPage());
            this.setText(this.attachment.getTitle());
        } else {
            this.setText("< attachment not selected >");
        }
        this.firePropertyChange(PROPERTY_ATTACHMENT, oldValue, this.attachment);
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
