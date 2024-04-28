package org.myx.fileIo.metadata;

public class ColumnMetaData {
    private String columnName;
    private String columnType;
    private boolean isPrimaryKey;
    private boolean isForeignKey;
    private boolean isUnique;
    private boolean isNotNull;
    private boolean isCheck;
    private boolean isDefault;

    public ColumnMetaData(String columnName, String columnType, boolean isPrimaryKey,
                          boolean isForeignKey, boolean isUnique, boolean isNotNull,
                          boolean isCheck, boolean isDefault) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.isPrimaryKey = isPrimaryKey;
        this.isForeignKey = isForeignKey;
        this.isUnique = isUnique;
        this.isNotNull = isNotNull;
        this.isCheck = isCheck;
        this.isDefault = isDefault;
    }

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

    public boolean isPrimaryKey() {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        isPrimaryKey = primaryKey;
    }

    public boolean isForeignKey() {
        return isForeignKey;
    }

    public void setForeignKey(boolean foreignKey) {
        isForeignKey = foreignKey;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public void setNotNull(boolean notNull) {
        isNotNull = notNull;
    }


    public boolean isCheck() {
        return isCheck;
    }

    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    @Override
    public String toString() {
        return "ColumnMetaData{" +
                "columnName='" + columnName + '\'' +
                ", columnType='" + columnType + '\'' +
                ", isPrimaryKey=" + isPrimaryKey +
                ", isForeignKey=" + isForeignKey +
                ", isUnique=" + isUnique +
                ", isNotNull=" + isNotNull +
                ", isCheck=" + isCheck +
                ", isDefault=" + isDefault +
                '}';
    }




}
