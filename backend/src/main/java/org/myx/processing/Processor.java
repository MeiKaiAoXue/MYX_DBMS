package org.myx.processing;

import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
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
import java.util.List;

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
                processInsert((Insert) statement);
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

    private static void processInsert(Insert statement) throws IOException {
        DBMetaData db = (DBMetaData) FileUtils.readObjectFromFile("./db.txt");

        String tableName = statement.getTable().getName();
        TableMetaData1 table = db.getTable(tableName);
        List<List<Object>> values = (List<List<Object>>) FileUtils.readObjectFromFile("./" + tableName + ".txt");

        if (table == null) {
            Logging.log("Table " + tableName + " does not exist");
            Logging.log("Please create the table before inserting data");
            return;
        }

        // 单行插入
        if (statement.getColumns() != null) {
            ExpressionList<Column> columns = statement.getColumns();
            if (statement.getSelect() != null) {
                for (int i = 0; i < columns.size(); i++) {
                    String columnName = columns.get(i).getColumnName();
                    String columnType = table.getColumnType(columnName);
                    String value = statement.getSelect().getValues().getExpressions().get(i).toString();
                    // 判断个数是否匹配,个数不匹配除了default全部写null
                    // Todo 现在只支持全部column都有值，不支持部分有值，也就不存在default判断
                    if (columns.size() != statement.getSelect().getValues().getExpressions().size()) {
                        Logging.log("Number of columns does not match number of values");
                        return;
                    }
                    // 判断每个类型是否匹配
                    if (!isValid(value, columnType)) {
                        Logging.log("Value " + value + " is not valid for column " + columnName + " of type " + columnType);
                        return;
                    }
                    // 判断约束条件是否匹配
                    if (checkConstraints(db, table, values, columnName, value)) {
                        Logging.log("Constraint check failed for column " + columnName + " with value " + value);
                        return;
                    }
                }
            }
        }
    }

    private static boolean checkConstraints(DBMetaData db, TableMetaData1 tableMD, List<List<Object>> values, String columnName, String value) {
        List<TableMetaData1.ConstraintsMetaData> constraints = tableMD.getConstraints();
        int index = tableMD.getColumns().indexOf(columnName);
        for (TableMetaData1.ConstraintsMetaData constraint : constraints) {
            // 判断是否是这个字段的约束
            if (!constraint.getConstraintName().contains(columnName))
            {
                continue;
            }
            String constraintCondition = "";
            switch (constraint.getConstraintType()) {
                case "PRIMARY KEY":
                    // 判断unique和not null
                    for (List<Object> row : values) {
                        if (row.get(index).equals(value)) {
                            return false;
                        }
                    }
                    if (value == null || value.isEmpty())
                    {
                        return false;
                    }
                    break;
                case "NOT NULL":
                    if (value == null || value.isEmpty())
                    {
                        return false;
                    }
                    break;
                case "UNIQUE":
                    for (List<Object> row : values) {
                        if (row.get(index).equals(value)) {
                            return false;
                        }
                    }
                    break;
                case "CHECK":
                    constraintCondition = constraint.getConstraintCondition();
                    constraintCondition = constraintCondition.replaceAll("\\((.*?)\\)", "$1");
                    // int
                    try {
                        int num = Integer.parseInt(value);
                        if (constraintCondition.contains(">")) {
                            constraintCondition = constraintCondition.replaceAll("\\((.*?)\\)", "$1");
                            String[] list = constraintCondition.split(">");
                            if (list.length < 2) {
                                System.out.println("Invalid format: no '>' character found");
                            } else {
                                try {
                                    num = Integer.parseInt(list[1].trim());
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid format: cannot parse '" + list[1] + "' as an integer");
                                }
                            }
                            if (Integer.parseInt(value) <= num) {
                                return false;
                            }
                        } else if (constraintCondition.contains("<")) {
                            constraintCondition = constraintCondition.replaceAll("\\((.*?)\\)", "$1");
                            String[] list = constraintCondition.split("<");
                            if (list.length < 2) {
                                System.out.println("Invalid format: no '<' character found");
                            } else {
                                try {
                                    num = Integer.parseInt(list[1].trim());
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid format: cannot parse '" + list[1] + "' as an integer");
                                }
                            }
                            if (Integer.parseInt(value) >= num) {
                                return false;
                            }
                        } else if (constraintCondition.contains("=")) {
                            constraintCondition = constraintCondition.replaceAll("\\((.*?)\\)", "$1");
                            String[] list = constraintCondition.split("=");
                            if (list.length < 2) {
                                System.out.println("Invalid format: no '=' character found");
                            } else {
                                try {
                                    num = Integer.parseInt(list[1].trim());
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid format: cannot parse '" + list[1] + "' as an integer");
                                }
                            }
                            if (Integer.parseInt(value) != num) {
                                return false;
                            }
                        } else if (constraintCondition.contains("!=")) {
                            constraintCondition = constraintCondition.replaceAll("\\((.*?)\\)", "$1");
                            String[] list = constraintCondition.split("!=");
                            if (list.length < 2) {
                                System.out.println("Invalid format: no '!=' character found");
                            } else {
                                try {
                                    num = Integer.parseInt(list[1].trim());
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid format: cannot parse '" + list[1] + "' as an integer");
                                }
                            }
                            if (Integer.parseInt(value) == num) {
                                return false;
                            }
                        } else if (constraintCondition.contains(">=")) {
                            constraintCondition = constraintCondition.replaceAll("\\((.*?)\\)", "$1");
                            String[] list = constraintCondition.split(">=");
                            if (list.length < 2) {
                                System.out.println("Invalid format: no '>=' character found");
                            } else {
                                try {
                                    num = Integer.parseInt(list[1].trim());
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid format: cannot parse '" + list[1] + "' as an integer");
                                }
                            }
                            if (Integer.parseInt(value) < num) {
                                return false;
                            }
                        } else if (constraintCondition.contains("<=")) {
                            constraintCondition = constraintCondition.replaceAll("\\((.*?)\\)", "$1");
                            String[] list = constraintCondition.split("<=");
                            if (list.length < 2) {
                                System.out.println("Invalid format: no '<=' character found");
                            } else {
                                try {
                                    num = Integer.parseInt(list[1].trim());
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid format: cannot parse '" + list[1] + "' as an integer");
                                }
                            }
                            if (Integer.parseInt(value) > num) {
                                return false;
                            }
                        } else {
                            System.out.println("非比较判断符号");
                        }
                    } catch (NumberFormatException e) {
                        return false;
                    }
                    break;
//                case "DEFAULT":
//                    if (constraint.getConstraintCondition() == null) {
//                        return false;
//                    }
//                    break;
                case "FOREIGN KEY":
                    constraintCondition = constraint.getConstraintCondition();
                    String[] parts = constraintCondition.split("\\(|\\)");
                    String beforeParenthesis = parts[0];
                    String insideParenthesis = parts[1];
                    TableMetaData1 referencedTable = db.getTable(beforeParenthesis);
                    if (referencedTable == null) {
                        System.out.println("Referenced table " + beforeParenthesis + " does not exist");
                        return false;
                    }
                    String[] insideParts = insideParenthesis.split(",");
                    // 单外键
                    List<List<Object>> referencedValues = (List<List<Object>>) FileUtils.readObjectFromFile("./" + referencedTable.getTableName() + ".txt");
                    if (insideParts.length == 1) {
                        int fkIndex = referencedTable.getColumns().indexOf(insideParts[0]);
                        for (List<Object> row : referencedValues) {
                            if (row.get(fkIndex).equals(value)) {
                                return true;
                            }
                        }
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    // 给insert判断值和字段类型是否匹配，DATE类型暂且当作String处理
    private static boolean isValid(String value, String columnType) {
        // Check if the value is valid for the column type
        // For example, if the column type is INT, check if the value can be parsed to an integer
        if (columnType.equals("INT")) {
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (columnType.equals("VARCHAR")) {
            // Check if the value is a string
            return value.startsWith("'") && value.endsWith("'");
        }

        return true;
    }

    /**
     * 处理Create Table语句
     * @param createTable
     */
    private static void processCreate(CreateTable createTable) throws IOException {
        DBMetaData db = (DBMetaData) FileUtils.readObjectFromFile("./db.txt");

        String tableName = createTable.getTable().getName();
        TableMetaData1 checkTable = db.getTable(tableName);
        if (checkTable != null) {
            Logging.log("Table " + tableName + " already exists");
            return;
        }

        TableMetaData1 newTableMetaData = new TableMetaData1(tableName);
        createTable.getColumnDefinitions().forEach(columnDefinition -> {
            // 给表元数据添加列元数据
            if (columnDefinition.getColDataType().getDataType().equals("VARCHAR"))
            {
                int index = columnDefinition.getColDataType().getArgumentsStringList().indexOf("VARCHAR");
                String columnType = columnDefinition.getColDataType().getDataType() + "(" + columnDefinition.getColDataType().getArgumentsStringList().get(index + 1) + ")";
                newTableMetaData.addColumn(columnDefinition.getColumnName(), columnType);
            }
            else {
                newTableMetaData.addColumn(columnDefinition.getColumnName(), columnDefinition.getColDataType().getDataType());
            }

            // 处理列约束
            for (ConstraintType constraint : ConstraintType.values()) {
                String constraintName = constraint.name().replace("_", " ");
                // 先判断有没有这个约束
                if (columnDefinition.toString().contains(constraintName)) {
                    // 再判断是哪个约束
                    switch (constraint) {
                        case PRIMARY_KEY:
                            newTableMetaData.addConstraint("PK_" + columnDefinition.getColumnName(), "PRIMARY KEY", null);
                            break;
                        case NOT_NULL:
                            newTableMetaData.addConstraint("NN_" + columnDefinition.getColumnName(), "NOT NULL", null);
                            break;
                        case UNIQUE:
                            newTableMetaData.addConstraint("UK_" + columnDefinition.getColumnName(), "UNIQUE", null);
                            break;
                        case CHECK:
                            int checkIndex = columnDefinition.getColumnSpecs().indexOf("CHECK");
                            String checkCondition = columnDefinition.getColumnSpecs().get(checkIndex + 1);
                            newTableMetaData.addConstraint("CK_" + columnDefinition.getColumnName(), "CHECK", checkCondition);
                            break;
                        case DEFAULT:
                            int defaultIndex = columnDefinition.getColumnSpecs().indexOf("DEFAULT");
                            String defaultCondition = columnDefinition.getColumnSpecs().get(defaultIndex + 1);
                            newTableMetaData.addConstraint("DF_" + columnDefinition.getColumnName(), "DEFAULT", defaultCondition);
                            break;
                        case REFERENCES:
                            int fkIndex = columnDefinition.getColumnSpecs().indexOf("REFERENCES");
                            String referenceCondition = columnDefinition.getColumnSpecs().get(fkIndex + 1) + columnDefinition.getColumnSpecs().get(fkIndex + 2);
                            newTableMetaData.addConstraint("FK_" + columnDefinition.getColumnName(), "FOREIGN KEY", referenceCondition);
                            break;
                    }
                }
            }

            // Todo: 处理表约束


        });
        db.addTable(newTableMetaData);
        FileUtils.writeObjectToFile(db, "./db.txt");

        Table table = new Table(tableName, newTableMetaData.getColumns());
        // 表文件中只存表对象中的values，不存表名和字段
        FileUtils.writeObjectToFile(table.getValues(), "./" + tableName + ".txt");
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
