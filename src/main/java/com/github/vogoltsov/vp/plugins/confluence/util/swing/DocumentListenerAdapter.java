package com.github.vogoltsov.vp.plugins.confluence.util.swing;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public interface DocumentListenerAdapter extends DocumentListener {

    @Override
    default void insertUpdate(DocumentEvent e) {
        documentUpdate(e);
    }

    @Override
    default void removeUpdate(DocumentEvent e) {
        documentUpdate(e);
    }

    @Override
    default void changedUpdate(DocumentEvent e) {
        documentUpdate(e);
    }

    void documentUpdate(DocumentEvent e);

}
