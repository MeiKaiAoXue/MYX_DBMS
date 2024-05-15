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

    /**
     *构造函数，初始化数据库名称和表列表
     * @param dbName 数据库名称
     */
    public DBMetaData(String dbName) {
        this.dbName = dbName;
        this.tables = new ArrayList<>();
    }
//获取数据库名称
    public String getDbName() {
        return dbName;
    }
//设置数据库名称
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**获取所有表的元数据列表
     *
     * @return 表的元数据列表
     */
    public List<TableMetaData1> getTables() {
        return tables;
    }

    /**
     * 设置所有表的元数据列表
     * @param tables 表的元数据列表
     */
    public void setTables(List<TableMetaData1> tables) {
        this.tables = tables;
    }

    /**
     * 向数据库中添加表的元数据
     * @param table 要添加的表的元数据
     */
    public void addTable(TableMetaData1 table) {
        tables.add(table);
    }

    /**
     * 从数据库中移除指定的表的元数据
     * @param table 要移除的表的元数据
     */
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
