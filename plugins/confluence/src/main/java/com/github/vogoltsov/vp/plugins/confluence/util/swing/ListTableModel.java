package com.github.vogoltsov.vp.plugins.confluence.util.swing;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import java.util.function.BiFunction;

/**
 * @author Vitaly Ogoltsov &lt;vitaly.ogoltsov@me.com&gt;
 */
public class ListTableModel<R> extends AbstractTableModel {

    private final List<String> columns;
    private final List<R> rows = new Vector<>();
    private final BiFunction<R, Integer, Object> rowValueAccessor;


    public ListTableModel(List<String> columns, BiFunction<R, Integer, Object> rowValueAccessor) {
        Objects.requireNonNull(columns);
        Objects.requireNonNull(rowValueAccessor);
        this.columns = new Vector<>(columns);
        this.rowValueAccessor = rowValueAccessor;
    }


    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public String getColumnName(int column) {
        return columns.get(column);
    }


    @Override
    public int getRowCount() {
        return rows.size();
    }

    public R getRowAt(int rowIndex) {
        return rows.get(rowIndex);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return rowValueAccessor.apply(getRowAt(rowIndex), columnIndex);
    }


    public void clear() {
        int rowCount = this.rows.size();
        this.rows.clear();
        this.fireTableRowsDeleted(0, rowCount);
    }

    public void addRows(List<R> rows) {
        int insertIndex = rows.size();
        this.rows.addAll(rows);
        this.fireTableRowsInserted(insertIndex, rows.size());
    }

    public void setRows(List<R> rows) {
        this.clear();
        this.addRows(rows);
    }

}
