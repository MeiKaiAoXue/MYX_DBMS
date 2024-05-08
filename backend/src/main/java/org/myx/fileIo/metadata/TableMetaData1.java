package org.myx.fileIo.metadata;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 保存表的元数据
 */
public class TableMetaData1 implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tableName;
    private List<ColumnMetaData> columns;
    private List<ConstraintsMetaData> constraints;

    public TableMetaData1(String tableName) {
        this.tableName = tableName;
        this.columns = new ArrayList<>();
        this.constraints = new ArrayList<>();
    }


    public String getColumnType(String columnName) {
        for (ColumnMetaData column : columns) {
            if (column.columnName.equals(columnName)) {
                return column.columnType;
            }
        }
        return null;
    }

    public void addColumn(String columnName, String columnType) {
        ColumnMetaData column = new ColumnMetaData();
        column.columnName = columnName;
        column.columnType = columnType;
        this.columns.add(column);
    }

    public void dropColumn(List<ColumnMetaData>change){
        this.columns=change;
    }

    public void addConstraint(String constraintName, String constraintType, String constraintCondition) {
        ConstraintsMetaData constraint = new ConstraintsMetaData();
        constraint.constraintName = constraintName;
        constraint.constraintType = constraintType;
        constraint.constraintCondition = constraintCondition;
        this.constraints.add(constraint);
    }

    public static class ColumnMetaData implements Serializable{
//        private static final long serialVersionUID = 1L;
        private String columnName;
        private String columnType;

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnType() {
            return columnType;
        }

        public void setColumnType(String columnType) {
            this.columnType = columnType;
        }
    }

    public static class ConstraintsMetaData implements Serializable{
//        private static final long serialVersionUID = 1L;
        private String constraintName;
        private String constraintType;
        private String constraintCondition;

        public String getConstraintName() {
            return constraintName;
        }

        public void setConstraintName(String constraintName) {
            this.constraintName = constraintName;
        }
        public String getConstraintType() {
            return constraintType;
        }

        public void setConstraintType(String constraintType) {
            this.constraintType = constraintType;
        }

        public String getConstraintCondition() {
            return constraintCondition;
        }

        public void setConstraintCondition(String constraintCondition) {
            this.constraintCondition = constraintCondition;
        }
    }

    public String getTableName() {
        return tableName;
    }

    public List<ColumnMetaData> getColumns() {
        return columns;
    }

    public int getColumnIndex(String columnName) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).columnName.equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    public List<ConstraintsMetaData> getConstraints() {
        return constraints;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setColumns(List<ColumnMetaData> columns) {
        this.columns = columns;
    }

    public void setConstraints(List<ConstraintsMetaData> constraints) {
        this.constraints = constraints;
    }

}
