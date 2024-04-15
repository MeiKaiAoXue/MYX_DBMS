package org.myx.processing;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.myx.fileIo.FileUtils;
import org.myx.fileIo.Logging;
import org.myx.fileIo.metadata.ConstraintType;
import org.myx.fileIo.metadata.ConstraintsMetaData;
import org.myx.fileIo.metadata.DBMetaData;
import org.myx.fileIo.metadata.TableMetaData1;

import java.io.IOException;

public class Processor {

    public static void process(String sql){
        try {
            Statement statement = CCJSqlParserUtil.parse(sql.toUpperCase());

            if (statement instanceof CreateTable) {
                processCreate((CreateTable) statement);
            } else if (statement instanceof Drop) {
//                processDrop((Drop) statement);
            } else if (statement instanceof Alter) {
//                processAlter((Alter) statement);
            } else if (statement instanceof Select) {
//                processSelect((Select) statement);
            } else if (statement instanceof Insert) {
//                processInsert((Insert) statement);
            } else if (statement instanceof Update) {
//                processUpdate((Update) statement);
            } else if (statement instanceof Delete) {
//                processDelete((Delete) statement);
            } else {
                Logging.log("Error processing SQL: " + sql);
                throw new IllegalArgumentException("Unsupported SQL statement: " + sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processCreate(CreateTable createTable) {
        DBMetaData db = (DBMetaData) FileUtils.readObjectFromFile("./db.txt");

        String tableName = createTable.getTable().getName();
        TableMetaData1 newTable = new TableMetaData1(tableName);

        createTable.getColumnDefinitions().forEach(columnDefinition -> {
            // 给表元数据添加列元数据
            if (columnDefinition.getColDataType().getDataType().equals("VARCHAR"))
            {
                int index = columnDefinition.getColDataType().getArgumentsStringList().indexOf("VARCHAR");
                String columnType = columnDefinition.getColDataType().getDataType() + "(" + columnDefinition.getColDataType().getArgumentsStringList().get(index + 1) + ")";
                newTable.addColumn(columnDefinition.getColumnName(), columnType);
            }
            else {
                newTable.addColumn(columnDefinition.getColumnName(), columnDefinition.getColDataType().getDataType());
            }
            // 处理列约束
            for (ConstraintType constraint : ConstraintType.values())
            {
                if (columnDefinition.toString().contains(constraint.name().replace("_", " "))) {
                    newTable.addConstraint("PK_" + columnDefinition.getColumnName(), "PRIMARY KEY",
                            null);
                } else if (columnDefinition.toString().contains(constraint.name().replace("_", " "))) {
                    newTable.addConstraint("NN_" + columnDefinition.getColumnName(), "NOT NULL",
                            null);
                } else if (columnDefinition.toString().contains(constraint.name().replace("_", " "))) {
                    newTable.addConstraint("UK_" + columnDefinition.getColumnName(), "UNIQUE",
                            null);
                } else if (columnDefinition.toString().contains(constraint.name().replace("_", " "))) {
                    int index = columnDefinition.getColumnSpecs().indexOf("CHECK");
                    String checkCondition = columnDefinition.getColumnSpecs().get(index + 1);
                    newTable.addConstraint("CK_" + columnDefinition.getColumnName(), "CHECK",
                            checkCondition);
                } else if (columnDefinition.toString().contains(constraint.name().replace("_", " "))) {
                    int index = columnDefinition.getColumnSpecs().indexOf("DEFAULT");
                    String defaultCondition = columnDefinition.getColumnSpecs().get(index + 1);
                    newTable.addConstraint("DF_" + columnDefinition.getColumnName(), "DEFAULT",
                            defaultCondition);
                } else if (columnDefinition.toString().contains(constraint.name().replace("_", " "))) {
                    int index = columnDefinition.getColumnSpecs().indexOf("REFERENCES");
                    String referenceCondition = columnDefinition.getColumnSpecs().get(index + 1); // 此处的主表名及列名的格式为a2(A)
                    newTable.addConstraint("FK_" + columnDefinition.getColumnName(), "FOREIGN KEY",
                            referenceCondition);
                }
            }

        });

        db.addTable(newTable);

        FileUtils.writeObjectToFile(db, "./db.txt");
    }

//    private void processDrop(Drop drop) {
//        String tableName = drop.getName();
//        String filePath = tableMDFilePath + "/" + tableName + ".txt";
//        File file = new File(filePath);
//        if (file.exists()) {
//            file.delete();
//        }
//    }
//
//    private void processAlter(Alter alter) {
//        String tableName = alter.getTable().getName();
//        String filePath = tableMDFilePath + "/" + tableName + ".txt";
//        FileUtils.writeObjectToFile(alter, filePath);
//    }

    // 将约束条件list转换成以_分割的字符串
//    private String ListToString(List<String> list) {
//        if (list != null) {
//            String str = String.join(" ", list);
//            return str.replace(" ", "_").toUpperCase();
//        }
//        else return "";
//    }
}
