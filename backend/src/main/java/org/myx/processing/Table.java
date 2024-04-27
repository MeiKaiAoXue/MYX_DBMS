package org.myx.processing;

import org.myx.fileIo.metadata.TableMetaData1;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 保存数据的Table类
 */
public class Table {
    String tableName;
    List<TableMetaData1.ColumnMetaData> columns;
    // 行在外，列在内
    List<List<Object>> values;

    public Table(String tableName, List<TableMetaData1.ColumnMetaData> columns) {
        this.tableName = tableName;
        this.columns = columns;
        values = new ArrayList<>();
    }

    public void addRow(List<Object> row) {
        if (row.size() != columns.size()) {
            throw new IllegalArgumentException("Row size does not match column size");
        }
        values.add(row);
    }

    public void removeRow(int index) {
        values.remove(index);
    }

    public String getTableName() {
        return tableName;
    }

    public List<TableMetaData1.ColumnMetaData> getColumns() {
        return columns;
    }

    public List<List<Object>> getValues() {
        return values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return Objects.equals(tableName, table.tableName) && Objects.equals(columns, table.columns) && Objects.equals(values, table.values);
    }

}
