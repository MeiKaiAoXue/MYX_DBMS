package org.myx.fileIo.metadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 保存数据库中所有表的元数据
 */
public class DBMetaData implements Serializable {
    private static final long serialVersionUID = 1L;
    private String dbName;
    private List<TableMetaData1> tables;

    public DBMetaData(String dbName) {
        this.dbName = dbName;
        this.tables = new ArrayList<>();
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public List<TableMetaData1> getTables() {
        return tables;
    }

    public void setTables(List<TableMetaData1> tables) {
        this.tables = tables;
    }

    public void addTable(TableMetaData1 table) {
        tables.add(table);
    }

    public void removeTable(TableMetaData1 table) {
        tables.remove(table);
    }

    public String toString() {
        return "DBMetaData{" +
                "tables:" + tables +
                '}';
    }

    public TableMetaData1 getTable(String tableName) {
        for (TableMetaData1 table : tables) {
            if (table.getTableName().equals(tableName)) {
                return table;
            }
        }
        return null;
    }




}
