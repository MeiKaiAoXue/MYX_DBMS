package org.myx.processing;

import net.sf.jsqlparser.expression.Expression;
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

public class    Processor {

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
        List<List<Object>> all_values = (List<List<Object>>) FileUtils.readObjectFromFile("./" + tableName + ".txt");

        if (table == null) {
            Logging.log("Table " + tableName + " does not exist");
            Logging.log("Please create the table before inserting data");
            return;
        }

        List<TableMetaData1.ColumnMetaData> table_columns = table.getColumns();
        // 单行插入
        if (statement.getColumns() != null) {
            ExpressionList<Column> columns = statement.getColumns();
            if (statement.getSelect() != null) {
                // 判断insert语句中字段数和值数是否匹配
                int valueNum = statement.getSelect().getValues().getExpressions().size();
                if (columns.size() != valueNum) {
                    Logging.log("Number of columns does not match number of values");
                    return;
                }

                List<Object> row = new java.util.ArrayList<>();
                // 判断insert的字段数和tableMetaData中的字段数是否匹配
                if (columns.size() == table_columns.size()) {
                    System.out.println("insert字段数和tableMetaData字段数相等");
                    // 赋值到row中
                    for (int i = 0; i < columns.size(); i++) {
                        row.add(statement.getSelect().getValues().getExpressions().get(i).toString());
                    }

                    // 判断每个字段的值是否匹配
                    for (int i = 0; i < row.size(); i++) {
                        String columnName = table_columns.get(i).getColumnName();
                        String columnType = table.getColumnType(columnName);
                        String value = row.get(i).toString();

                        // Todo 现在只支持全部column都有值，不支持部分写DEFAULT

                        // 判断每个类型是否匹配
                        if (!isValid(value, columnType)) {
                            Logging.log("Value " + value + " is not valid for column " + columnName + " of type " + columnType);
                            return;
                        }
                        // 判断约束条件是否匹配
                        if (!checkConstraints(db, table, all_values, columnName, value)) {
                            Logging.log("Constraint check failed for column " + columnName + " with value " + value);
                            return;
                        }
                    }

                } else if (columns.size() < table_columns.size()) {
                    System.out.println("insert字段数小于tableMetaData字段数");
                    ExpressionList<?> expressions = statement.getSelect().getValues().getExpressions();
                    // 用null填充,假设元数据字段顺序和insert字段顺序一致
                    for (int i = 0; i < table_columns.size(); i++) {
                        boolean flag = false;
                        String tableColumnName = table_columns.get(i).getColumnName();
                        for (int j = 0; j < columns.size(); j++) {
                            String insertColumnName = columns.get(j).getColumnName();
                            if (tableColumnName.equals(insertColumnName)) {
                                flag = true;
                                row.add(expressions.get(j).toString().replace("(", "").replace(")", "").strip());
                                break;
                            }
                        }
                        if (!flag) {
                            row.add("NULL");
                        }
                    }


                    // 判断每个字段的值是否匹配
                    for (int i = 0; i < row.size(); i++) {
                        String columnName = table_columns.get(i).getColumnName();
                        String columnType = table.getColumnType(columnName);
                        String value = row.get(i).toString();

                        // Todo 现在只支持全部column都有值，不支持部分写DEFAULT

                        // 判断每个类型是否匹配
                        if (!isValid(value, columnType)) {
                            Logging.log("Value " + value + " is not valid for column " + columnName + " of type " + columnType);
                            return;
                        }
                        // 判断约束条件是否匹配
                        if (!checkConstraints(db, table, all_values, columnName, value)) {
                            Logging.log("Constraint check failed for column " + columnName + " with value " + value);
                            return;
                        }
                    }

                } else {
                    Logging.log("insert字段数大于tableMetaData字段数");
                    return;
                }

                if (row.size() == table_columns.size()) {
                    all_values.add(row);
                    FileUtils.writeObjectToFile(all_values, "./" + tableName + ".txt");
                    System.out.println("Inserted row: " + row);
                } else {
                    Logging.log("Row size does not match column size");
                }
            }
        }

    }

    private static boolean checkConstraints(DBMetaData db, TableMetaData1 tableMD,
                                            List<List<Object>> values, String columnName,
                                            String value) {
        List<TableMetaData1.ConstraintsMetaData> constraints = tableMD.getConstraints();
//        System.out.println("约束条件");
//        for (TableMetaData1.ConstraintsMetaData constraintsMetaData : constraints)
//        {
//            System.out.println(constraintsMetaData.getConstraintName());
//        }

        System.out.println("字段名：" + columnName);
        int index = tableMD.getColumnIndex(columnName);
        System.out.println("字段索引：" + index);
        for (TableMetaData1.ConstraintsMetaData constraint : constraints) {
            // 判断是否是这个字段的约束
            if (!constraint.getConstraintName().contains(columnName))
            {
                System.out.println("不是这个字段的约束");
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
                    if (value.equals("NULL") || value.isEmpty())
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
//                                System.out.println("Invalid format: " + value + " is not greater than " + num);
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
                    // Todo: 外键约束目前不要求
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
        System.out.println("字段类型是： " + columnType);
        if (columnType.equals("INT")) {
            // 判断是否为int
            try {
                Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (columnType.contains("VARCHAR")) {
            // 判断是否为长度合适的字符串

            int length = Integer.parseInt(columnType.replaceAll("[^0-9]", ""));
//            System.out.println("长度是： " + length);
            if (value.equals("NULL")) return true; // NULL当作字符串处理
            else if (!(value.startsWith("'") && value.endsWith("'"))) return false;

            if (value.length() - 2 > length) return false;
        } else if (columnType.contains("CHAR")) {
            // 判断是否为长度合适的字符串
            int length = Integer.parseInt(columnType.replaceAll("[^0-9]", ""));
            if (value.equals("NULL")) return true;
            else if (!(value.startsWith("'") && value.endsWith("'"))) return false;

            if (value.length() - 2 != length) return false;
        } else if (columnType.equals("DATE")) {
            // 判断是否为日期格式
            // Todo: 暂时不做日期格式判断,当作字符串处理
//            if (!(value.startsWith("'") && value.endsWith("'"))) return false;
//            if (value.length() != 10) return false;
//            String[] parts = value.substring(1, value.length() - 1).split("-");
//            if (parts.length != 3) return false;
//            try {
//                Integer.parseInt(parts[0]);
//                Integer.parseInt(parts[1]);
//                Integer.parseInt(parts[2]);
//            } catch (NumberFormatException e) {
//                return false;
//            }
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
