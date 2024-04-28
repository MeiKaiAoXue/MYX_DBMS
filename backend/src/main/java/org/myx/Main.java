package org.myx;

import org.myx.fileIo.FileUtils;
import org.myx.fileIo.metadata.DBMetaData;
import org.myx.fileIo.metadata.TableMetaData1;
import org.myx.processing.Processor;

public class Main {
    public static void main(String[] args) {
        FileUtils.initDB("./db.txt");
        String sql = "CREATE TABLE AA (column1 INT, column2 VARCHAR(255));";
        String sql2 = "Create Table BB (column1 INT DEFAULT 2);";
        String sql3 = "CREATE TABLE AA (column1 INT, column2 VARCHAR(255));";
        Processor.process(sql);
        Processor.process(sql2);
        System.out.println("Create Table Test passed");

        DBMetaData dbMetaData = (DBMetaData)FileUtils.readObjectFromFile("./db.txt");
        System.out.println("Length of db: " + dbMetaData.getTables().size());
        for (TableMetaData1 table : dbMetaData.getTables()) {
            System.out.println("Table Name: " + table.getTableName());
            for (TableMetaData1.ColumnMetaData column : table.getColumns()) {
                System.out.println("Column: " + column.getColumnName() + " " + column.getColumnType());
            }
            for (TableMetaData1.ConstraintsMetaData constraint : table.getConstraints()) {
                System.out.println("Constraint: " + constraint.getConstraintName() + " " + constraint.getConstraintType() + " " + constraint.getConstraintCondition());
            }
        }
    }
}