package com.github.vogoltsov.vp.plugins.common.swing;

import lombok.Setter;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxEditor;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.MutableComboBoxModel;
import javax.swing.Timer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;
import java.util.function.Function;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class AutoCompleteComboBox<T> extends JComboBox<T> {

    private static final int DEFAULT_SEARCH_DELAY = 1000;

    private static final String PROPERTY_EMPTY_ITEM_LABEL = "emptyItemLabel";


    private final Function<String, Collection<T>> searchFunction;
    private final Function<T, String> labelFunction;

    @Setter
    private boolean emptyItemAllowed = false;
    private String emptyItemLabel;

    private boolean reloadItemsFired = false;
    private boolean selectingItem = false;


    public AutoCompleteComboBox(Function<String, Collection<T>> searchFunction, Function<T, String> labelFunction) {
        super(new AutoCompleteComboBoxModel<>());

        Objects.requireNonNull(searchFunction);
        Objects.requireNonNull(labelFunction);
        this.searchFunction = searchFunction;
        this.labelFunction = labelFunction;

        this.setEditor(new AutoCompleteComboBoxEditor(this.getEditor()));
        this.setRenderer(new AutoCompleteComboBoxRenderer());

        // remove show popup button
        Arrays.stream(getComponents())
                .filter(component -> component instanceof JButton)
                .forEach(this::remove);

        this.setEditable(true);
    }

    public void setEmptyItemLabel(String emptyItemLabel) {
        if (Objects.equals(this.emptyItemLabel, emptyItemLabel)) {
            return;
        }
        String oldValue = this.emptyItemLabel;
        this.emptyItemLabel = emptyItemLabel;
        this.firePropertyChange(PROPERTY_EMPTY_ITEM_LABEL, oldValue, this.emptyItemLabel);
    }

    private void reloadItems() {
        //noinspection unchecked
        final String searchText = ((AutoCompleteComboBoxEditor) this.getEditor()).getEditorComponent().getText();
        final Collection<T> searchResults = this.searchFunction.apply(searchText);

        this.reloadItemsFired = true;
        setPopupVisible(false);
        removeAllItems();
        if (emptyItemAllowed) {
            addItem(null);
        }
        searchResults.forEach(this::addItem);
        setPopupVisible(getModel().getSize() > 0);
        this.reloadItemsFired = false;
    }

    @Override
    public void removeAllItems() {
        AutoCompleteComboBoxModel<T> model = (AutoCompleteComboBoxModel<T>) dataModel;
        model.removeAllElements();
    }

    @Override
    public AutoCompleteComboBoxModel<T> getModel() {
        return (AutoCompleteComboBoxModel<T>) super.getModel();
    }

    @Override
    public T getSelectedItem() {
        return getModel().getSelectedItem();
    }

    @Override
    public void setSelectedItem(Object anObject) {
        this.selectingItem = true;
        super.setSelectedItem(anObject);
        this.selectingItem = false;
    }

    /**
     * Override default {@link JComboBox} behavior so selected item is not changed when items list is updated.
     */
    @Override
    public void contentsChanged(ListDataEvent e) {
        /* do nothing */
    }

    /**
     * Override default {@link JComboBox} behavior so selected item is not changed when items list is updated.
     */
    @Override
    public void intervalAdded(ListDataEvent e) {
        /* do nothing */
    }

    /**
     * Override default {@link JComboBox} behavior so selected item is not changed when items list is updated.
     */
    @Override
    public void intervalRemoved(ListDataEvent e) {
        /* do nothing */
    }

    /**
     * Override default {@link JComboBox} behavior so selected item is not changed when items list is updated.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        /* do nothing */
    }

    /**
     * Override default {@link JComboBox} behavior so selected item is not changed when items list is updated.
     */
    @Override
    public void configureEditor(ComboBoxEditor anEditor, Object anItem) {
        if (this.reloadItemsFired) {
            return;
        }
        super.configureEditor(anEditor, anItem);
    }


    /**
     * Re-implementation of {@link javax.swing.DefaultComboBoxModel} which does not change selected item
     * when a list of elements is changed.
     */
    private static class AutoCompleteComboBoxModel<T> extends AbstractListModel<T> implements MutableComboBoxModel<T>, Serializable {

        private final Vector<T> objects = new Vector<>();

        private T selectedObject;


        @Override
        public void addElement(T item) {
            insertElementAt(item, objects.size());
        }

        @Override
        public void removeElement(Object obj) {
            //noinspection SuspiciousMethodCalls
            int index = objects.indexOf(obj);
            if (index != -1) {
                removeElementAt(index);
            }
        }

        @Override
        public void insertElementAt(T item, int index) {
            objects.insertElementAt(item, index);
            fireIntervalAdded(this, index, index);
        }

        @Override
        public void removeElementAt(int index) {
            objects.removeElementAt(index);
            fireIntervalRemoved(this, index, index);
        }

        void removeAllElements() {
            if (objects.size() > 0) {
                int firstIndex = 0;
                int lastIndex = objects.size() - 1;
                objects.removeAllElements();
                fireIntervalRemoved(this, firstIndex, lastIndex);
            }
        }

        @Override
        public void setSelectedItem(Object anItem) {
            if (!Objects.equals(selectedObject, anItem)) {
                //noinspection unchecked
                this.selectedObject = (T) anItem;
                fireContentsChanged(this, -1, -1);
            }
        }

        @Override
        public T getSelectedItem() {
            return selectedObject;
        }

        @Override
        public int getSize() {
            return objects.size();
        }

        @Override
        public T getElementAt(int index) {
            if (index >= 0 && index < objects.size()) {
                return objects.elementAt(index);
            } else {
                return null;
            }
        }
    }


    /**
     * Wrapper around {@link ComboBoxEditor} that adds autocomplete-specific logic like search delay.
     */
    private class AutoCompleteComboBoxEditor implements ComboBoxEditor, FocusListener, PopupMenuListener {

        private final ComboBoxEditor comboBoxEditorDelegate;
        private final Timer searchTimer;


        private boolean settingText = false;


        AutoCompleteComboBoxEditor(ComboBoxEditor delegate) {
            Objects.requireNonNull(delegate);
            this.comboBoxEditorDelegate = delegate;

            // create timer which reloads combo box items on user input delay
            this.searchTimer = new Timer(DEFAULT_SEARCH_DELAY, (timerEvent) -> AutoCompleteComboBox.this.reloadItems());
            this.searchTimer.setRepeats(false);

            // restart search timer when text in editor is changed
            getEditorComponent().getDocument().addDocumentListener((DocumentListenerAdapter) e -> {
                if (!reloadItemsFired && !selectingItem && !settingText) {
                    this.searchTimer.restart();
                }
            });
            // register as focus listener on editor component
            getEditorComponent().addFocusListener(this);
            // register as combobox popup menu listener
            AutoCompleteComboBox.this.addPopupMenuListener(this);
            // listen on empty label changes
            AutoCompleteComboBox.this.addPropertyChangeListener(PROPERTY_EMPTY_ITEM_LABEL, e -> updateItem());
            // set default value
            setItem(null);
        }

        @Override
        public JTextComponent getEditorComponent() {
            return (JTextComponent) comboBoxEditorDelegate.getEditorComponent();
        }

        private void updateItem() {
            setItem(AutoCompleteComboBox.this.getSelectedItem());
        }

        @Override
        public void setItem(Object anObject) {
            //noinspection unchecked
            String label = Optional.ofNullable((T) anObject)
                    .map(AutoCompleteComboBox.this.labelFunction)
                    .orElse(getEditorComponent().hasFocus() ? null : AutoCompleteComboBox.this.emptyItemLabel);
            this.settingText = true;
            this.comboBoxEditorDelegate.setItem(label);
            this.settingText = false;
        }

        @Override
        public Object getItem() {
            return comboBoxEditorDelegate.getItem();
        }

        @Override
        public void selectAll() {
            comboBoxEditorDelegate.selectAll();
        }

        void deselect() {
            getEditorComponent().select(0, 0);
        }

        @Override
        public void addActionListener(ActionListener l) {
            comboBoxEditorDelegate.addActionListener(l);
        }

        @Override
        public void removeActionListener(ActionListener l) {
            comboBoxEditorDelegate.removeActionListener(l);
        }

        @Override
        public void focusGained(FocusEvent e) {
            updateItem();
            if (AutoCompleteComboBox.this.getSelectedItem() == null) {
                AutoCompleteComboBox.this.reloadItems();
            } else {
                selectAll();
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            updateItem();
            deselect();
        }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            /* do nothing */
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            // don't update editor text on items reload
            if (reloadItemsFired) {
                return;
            }
            updateItem();
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            /* do nothing */
        }

    }


    /**
     * Wrapper around {@link ListCellRenderer} that uses {@link AutoCompleteComboBox#labelFunction}
     * to convert items to strings.
     */
    private class AutoCompleteComboBoxRenderer implements ListCellRenderer<T> {

        private final ListCellRenderer<Object> delegate;

        AutoCompleteComboBoxRenderer() {
            this.delegate = new DefaultListCellRenderer();
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends T> list, T value, int index, boolean isSelected, boolean cellHasFocus) {
            String label = Optional.ofNullable(value)
                    .map(AutoCompleteComboBox.this.labelFunction)
                    .orElse(AutoCompleteComboBox.this.emptyItemLabel);
            return delegate.getListCellRendererComponent(list, label, index, isSelected, cellHasFocus);
        }

    }


}
