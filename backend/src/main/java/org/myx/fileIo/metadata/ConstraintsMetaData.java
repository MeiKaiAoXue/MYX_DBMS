package org.myx.fileIo.metadata;

import java.util.List;
import java.util.Objects;

public class ConstraintsMetaData {
    public enum ConstraintType {
        PRIMARY_KEY,
        UNIQUE,
        FOREIGN_KEY,
        CHECK,
        NOT_NULL,
        DEFAULT
    }

    private String constraintName;
    private ConstraintType constraintType;
    private String tableName;
    private List<String> columnNames;
    private String constraintCondition;

    public ConstraintsMetaData(String constraintName, ConstraintType constraintType, String tableName, List<String> columnNames, String constraintCondition) {
        this.constraintName = constraintName;
        this.constraintType = constraintType;
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.constraintCondition = constraintCondition;
    }

    public String getConstraintName() {
        return constraintName;
    }

    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    public ConstraintType getConstraintType() {
        return constraintType;
    }

    public void setConstraintType(ConstraintType constraintType) {
        this.constraintType = constraintType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public String getConstraintCondition() {
        return constraintCondition;
    }

    public void setConstraintCondition(String constraintCondition) {
        this.constraintCondition = constraintCondition;
    }

    @Override
    public String toString() {
        return "ConstraintsMetaData{" +
                "constraintName='" + constraintName + '\'' +
                ", constraintType=" + constraintType +
                ", tableName='" + tableName + '\'' +
                ", columnNames=" + columnNames +
                ", constraintCondition='" + constraintCondition + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConstraintsMetaData that = (ConstraintsMetaData) o;
        return Objects.equals(constraintName, that.constraintName) &&
                constraintType == that.constraintType &&
                Objects.equals(tableName, that.tableName) &&
                Objects.equals(columnNames, that.columnNames) &&
                Objects.equals(constraintCondition, that.constraintCondition);
    }


}
