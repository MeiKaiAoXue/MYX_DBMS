package org.myx.fileIo.metadata;

import java.util.List;

public class TableMetaData {
    private String tableName;
    private List<ColumnMetaData> columns;
    private List<ConstraintsMetaData> constraints;

    public TableMetaData(String tableName, List<ColumnMetaData> columns, List<ConstraintsMetaData> constraints) {
        this.tableName = tableName;
        this.columns = columns;
        this.constraints = constraints;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ColumnMetaData> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnMetaData> columns) {
        this.columns = columns;
    }

    public List<ConstraintsMetaData> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<ConstraintsMetaData> constraints) {
        this.constraints = constraints;
    }

    public String toString() {
        return "TableMetaData{" +
                "tableName='" + tableName + '\'' +
                ", columns:" + columns +
                ", constraints:" + constraints +
                '}';
    }

    public void addColumn(ColumnMetaData column) {
        columns.add(column);
    }

    public void addConstraint(ConstraintsMetaData constraint) {
        constraints.add(constraint);
    }

    public void removeColumn(ColumnMetaData column) {
        columns.remove(column);
    }

    public void removeConstraint(ConstraintsMetaData constraint) {
        constraints.remove(constraint);
    }

    public ColumnMetaData getColumn(String columnName) {
        for (ColumnMetaData column : columns) {
            if (column.getColumnName().equals(columnName)) {
                return column;
            }
        }
        return null;
    }


}
