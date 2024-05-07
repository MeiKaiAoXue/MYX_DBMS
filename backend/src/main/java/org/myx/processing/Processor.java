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
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.jetbrains.annotations.NotNull;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.myx.fileIo.FileUtils;
import org.myx.fileIo.Logging;
import org.myx.fileIo.metadata.ConstraintType;
import org.myx.fileIo.metadata.ConstraintsMetaData;
import org.myx.fileIo.metadata.DBMetaData;
import org.myx.fileIo.metadata.TableMetaData1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Processor {

    public static void process(String sql) {
        try {
            Statement statement = CCJSqlParserUtil.parse(sql.toUpperCase());

            if (statement instanceof CreateTable) {
                processCreate((CreateTable) statement);
            } else if (statement instanceof Drop) {
//                processDrop((Drop) statement);
            } else if (statement instanceof Alter) {
//                processAlter((Alter) statement);
            } else if (statement instanceof Select) {
                processSelect((Select) statement);
            } else if (statement instanceof Insert) {
                processInsert((Insert) statement);
            } else if (statement instanceof Update) {
                processUpdate((Update) statement);
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

    /**
     * 处理update Table语句
     *
     * @param Update
     */
    private static void processUpdate(Update statement) throws IOException {
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
        //UPDATE AA SET column1 = 2 where column2 = 'hello'
        System.out.println(statement.getTable());
        //(Update) CCJSqlParserUtil.parse(statement);
        System.out.println("【更新目标表】：" + statement.getTable());
        List<UpdateSet> updateSets = statement.getUpdateSets();
        for (UpdateSet updateSet : updateSets) {
            System.out.println("【更新字段】：" + updateSet.getColumns());
            System.out.println("【更新字】：" + updateSet.getValues());
        }
        System.out.println("【更新条件】：" + statement.getWhere());
        System.out.println("--------------------------------------------------------");
        String changeValue = updateSets.get(0).getValues().toString();
        //找到对应的列
        int changeColumnIndex = -9999;
        for (int i = 0; i < table_columns.size(); i++) {
            //只有一列更新，所以updateSets.get(0)
            if (table_columns.get(i).getColumnName().equals(updateSets.get(0).getColumns().toString())) {
                changeColumnIndex = i;
                break;
            }
        }
        //找到all_values中的对应列修改值
        //判断每个字段的值是否匹配
        String columnName = table_columns.get(changeColumnIndex).getColumnName();
        if (!isValid(updateSets.get(0).getValues().toString(), table.getColumnType(columnName))) {
            Logging.log("Value " + updateSets.get(0).getValues().toString() + " is not valid for column " + columnName + " of type " + table.getColumnType(columnName));
            return;
        } else {
            //字段值匹配测试输出
            System.out.println("Value " + updateSets.get(0).getValues().toString() + " is valid for column " + columnName + " of type " + table.getColumnType(columnName));
        } // 判断约束条件是否匹配
        if (!checkConstraints(db, table, all_values, columnName, updateSets.get(0).getValues().toString())) {
            Logging.log("Constraint check failed for column " + columnName + " with value " + updateSets.get(0).getValues().toString());
            return;
        } else {
            //约束匹配测试输出
            System.out.println("Constraint check passed for column " + updateSets.get(0).getValues().toString() + " is valid for column " + columnName + " of type " + table.getColumnType(columnName));
        }

        //对比where确定修改行坐标
        String whereStatement = statement.getWhere().toString();
        //String input = "COLUMN2 = 'HELLO'";

        // 查找等号的位置
        int equalSignIndex = whereStatement.indexOf('=');
        String whereColumnName=null;
        String whereValue=null;
        String equalSign=null;
        // 检查是否找到了等号
        if (equalSignIndex != -1) {
            // 提取列名（等号之前的内容）
            whereColumnName = whereStatement.substring(0, equalSignIndex).trim();
            // 提取值（等号之后的内容，并去掉前后的引号）
            whereValue = whereStatement.substring(equalSignIndex + 1).trim();
            // 去除值两边的引号（如果有的话）
            if (whereValue.startsWith("'") && whereValue.endsWith("'")) {
                whereValue = whereValue.substring(1, whereValue.length() - 1);
            }

            // 等号本身
            equalSign = "=";

            // 打印结果
            System.out.println("Column Name: " + whereColumnName);
            System.out.println("Equal Sign: " + equalSign);
            System.out.println("Value: " + whereValue);
        } else {
            System.out.println("No equal sign found in the string.");
        }
        int rowIndex=-9999;
        int whereColIndex=-9999;
        for(int i=0;i<table_columns.size();i++){
            if(table_columns.get(i).getColumnName().toString().equals(whereColumnName)){
                whereColIndex=i;
                break;
            }
        }
        if(whereColIndex<0){
            Logging.log("where 语句中的列不存在 ");
            System.out.println("where 语句中的列不存在 ");
            return;
        }
        for(int i=0;i<all_values.size();i++){
            String compareValue=all_values.get(i).get(whereColIndex).toString().replace("'", "");
            if(compareValue.equals(whereValue)){
                rowIndex=i;
                break;
            }
        }
        //将all_values中的第rowIndex行的第changeColumnIndex列改为对应值
        all_values.get(rowIndex).set(changeColumnIndex,changeValue);
        FileUtils.writeObjectToFile(all_values, "./" + tableName + ".txt");

    }

    /**
     * 处理select Table语句
     * @param Select
     */
    private  static  void  processSelect(Select statement) throws  IOException {
        DBMetaData db = (DBMetaData) FileUtils.readObjectFromFile("./db.txt");
        PlainSelect plainSelect = statement.getPlainSelect();
//        System.out.println("【DISTINCT 子句】：" + plainSelect.getDistinct());
//        System.out.println("【查询字段】：" + plainSelect.getSelectItems());
//        System.out.println("【FROM 表】：" + plainSelect.getFromItem());
//        System.out.println("【WHERE 子句】：" + plainSelect.getWhere());
//        System.out.println("【JOIN 子句】：" + plainSelect.getJoins());
//        System.out.println("【LIMIT 子句】：" + plainSelect.getLimit());
//        System.out.println("【OFFSET 子句】：" + plainSelect.getOffset());
//        System.out.println("【ORDER BY 子句】：" + plainSelect.getOrderByElements());
        //获取查询列名
        List<String> selectItemStrings = plainSelect.getSelectItems().stream()
                .map(Object::toString) // 假设SelectItem<?>对象的toString()方法返回所需字符串
                .collect(Collectors.toList());
        //获取表名
        FromItem tableName=plainSelect.getFromItem();
        if(tableName==null){
            Logging.log("Table " + tableName + " does not exist");
            Logging.log("Please create the table before inserting data");
            return;
        }
        List<List<Object>> all_values = (List<List<Object>>) FileUtils.readObjectFromFile("./" + tableName + ".txt");
        //获取where条件
        Expression where=plainSelect.getWhere();

        //获取表中的所有列名
        TableMetaData1 table = db.getTable(tableName.toString());
        List<TableMetaData1.ColumnMetaData> table_columns = table.getColumns();

        List<Integer> indexList = new ArrayList<>();//指定输出的列名下标
        //正则处理where
        String beforeOperator = null;
        String operator = "";
        String afterOperator = null;
        if(where==null){
            //*查询
            if(selectItemStrings.get(0).equals("*")){
                for (int i=0;i<table_columns.size();i++) {
                    System.out.print(table_columns.get(i).getColumnName());
                    System.out.print("  ");
                }
                System.out.println();
                for (int rowIndex=0;rowIndex<all_values.size();rowIndex++){
                    for (int colIndex=0;colIndex<all_values.get(rowIndex).size();colIndex++){
                        System.out.print(all_values.get(rowIndex).get(colIndex));
                        System.out.print("  ");
                    }
                    System.out.println();
                }

            }else{
                //指定列输出
                for (int x=0,j=0;x<table_columns.size()&&j<selectItemStrings.size();x++){
                    if(table_columns.get(x).getColumnName().equals(selectItemStrings.get(j))){
                        indexList.add(x);
                        j++;
                    }
                }
                if(indexList.size()!=selectItemStrings.size()){
                    //TODO:指明不存在的列名
                    System.out.println("有查询列不存在于表中");
                    Logging.log("有查询列不存在于表中");
                    return;
                }
                //输出
                for (int x=0;x<selectItemStrings.size();x++){
                    System.out.print(selectItemStrings.get(x));
                    System.out.print("    ");
                }
                System.out.println("");
                for(int rowIndex=0;rowIndex<all_values.size();rowIndex++){
                    for (int colIndex=0;colIndex<all_values.get(rowIndex).size();colIndex++){
                        for(int alIndex=0;alIndex<indexList.size();alIndex++){
                            if(colIndex==indexList.get(alIndex)){
                                //TODO:控制输出格式，使之对齐
                                System.out.print(all_values.get(rowIndex).get(colIndex));
                                System.out.print("    ");
                            }
                        }
                    }
                    System.out.println("");
                }
            }
        }else {
            //TODO:完成where后一个的判断 加上AND的多判断未完成
            String whereString = where.toString();
            // 定义正则表达式，匹配可能的运算符以及前后内容
            String regex = "(\\s*)(\\w+)(\\s*)(<=|>=|<>|=|<|>)(\\s*)('[^']*')(\\s*)";
            // 创建Pattern对象
            Pattern pattern = Pattern.compile(regex);

            // 创建Matcher对象
            Matcher matcher = pattern.matcher(whereString);

            // 尝试匹配
            if (matcher.matches()) {
                // 提取并打印结果
                beforeOperator = matcher.group(2); // 运算符前的部分
                operator = matcher.group(4); // 运算符
                afterOperator = matcher.group(6).replaceAll("'", ""); // 单引号内的部分，去除单引号

                // 输出结果
                System.out.println("Before operator: " + beforeOperator);
                System.out.println("Operator: " + operator);
                System.out.println("Inside quotes: " + afterOperator);
            } else {
                System.out.println("No match found in the input string.");
            }
            if(operator.equals("=")){
                if(selectItemStrings.get(0).equals("*")){
                    List<Integer> outPutRowNum = new ArrayList<>();
                    //处理where部分
                    int targetColIndex;
                    for(targetColIndex=0;targetColIndex<table_columns.size();targetColIndex++){
                        if(table_columns.get(targetColIndex).getColumnName().toUpperCase().equals(beforeOperator))
                            break;
                    }
                    if(targetColIndex==table_columns.size()){
                        //TODO:指明不存在的列名
                        System.out.println("where后有查询列不存在于表中");
                        Logging.log("where后有查询列不存在于表中");
                        return;
                    }
                    for (int rowIndex=0;rowIndex<all_values.size();rowIndex++){
                        String value = all_values.get(rowIndex).get(targetColIndex).toString().toUpperCase();
                        String compareValue=value.replace("'", "");
                        if(compareValue.equals(afterOperator))
                            outPutRowNum.add(rowIndex);//符合条件的行数
                    }
                    //output
                    for (int i=0;i<table_columns.size();i++) {
                        System.out.print(table_columns.get(i).getColumnName());
                        System.out.print("  ");
                    }
                    System.out.print("\n");

                    for (int rowIndex=0;rowIndex<outPutRowNum.size();rowIndex++){
                        for (int colIndex=0;colIndex<all_values.get(outPutRowNum.get(rowIndex)).size();colIndex++){
                            System.out.print(all_values.get(outPutRowNum.get(rowIndex)).get(colIndex));
                            System.out.print("  ");
                        }
                        System.out.print("\n");
                    }
                }
                else{
                    //指定列输出，处理select后的指定列
                    for (int x=0,j=0;x<table_columns.size()&&j<selectItemStrings.size();x++){
                        if(table_columns.get(x).getColumnName().equals(selectItemStrings.get(j))){
                            indexList.add(x);
                            j++;
                        }
                    }
                    if(indexList.size()!=selectItemStrings.size()){
                        //TODO:指明不存在的列名
                        System.out.println("有查询列不存在于表中");
                        Logging.log("有查询列不存在于表中");
                        return;
                    }
                    //where部分处理
                    List<Integer> outPutRowNum = new ArrayList<>();
                    int targetColIndex;
                    for(targetColIndex=0;targetColIndex<table_columns.size();targetColIndex++){
                        if(table_columns.get(targetColIndex).getColumnName().equals(beforeOperator))
                            break;
                    }

                    if(targetColIndex==table_columns.size()){
                        //TODO:指明不存在的列名
                        System.out.println("where后有查询列不存在于表中");
                        Logging.log("where后有查询列不存在于表中");
                        return;
                    }
                    for (int rowIndex=0;rowIndex<all_values.size();rowIndex++){
                        String value = all_values.get(rowIndex).get(targetColIndex).toString().toUpperCase();
                        String compareValue=value.replace("'", "");
                        if(compareValue.equals(afterOperator))
                            outPutRowNum.add(rowIndex);//符合条件的行数
                    }

                    //output
                    for (int i=0;i<indexList.size();i++) {
                        System.out.print(table_columns.get(indexList.get(i)).getColumnName());
                        System.out.print("  ");
                    }
                    System.out.print("\n");
                    for (int rowIndex=0;rowIndex<outPutRowNum.size();rowIndex++){
                        for (int colIndex=0;colIndex<indexList.size();colIndex++){
                            System.out.print(all_values.get(outPutRowNum.get(rowIndex)).get(indexList.get(colIndex)));
                            System.out.print("  ");
                        }
                        System.out.print("\n");
                    }

                }
            } else if (operator.equals(">")) {
                if(selectItemStrings.get(0).equals("*")){
                    List<Integer> outPutRowNum = new ArrayList<>();
                    int targetColIndex;
                    for(targetColIndex=0;targetColIndex<table_columns.size();targetColIndex++){
                        if(table_columns.get(targetColIndex).getColumnName().equals(beforeOperator))
                            break;
                    }

                    if(targetColIndex==table_columns.size()){
                        //TODO:指明不存在的列名
                        System.out.println("where后有查询列不存在于表中");
                        Logging.log("where后有查询列不存在于表中");
                        return;
                    }

                    for (int rowIndex=0;rowIndex<all_values.size();rowIndex++){
                        //不同处
                        String value=all_values.get(rowIndex).get(targetColIndex).toString().toUpperCase();
                        String compareValue=value.replace("'", "");
                        if(compareValue.compareTo(afterOperator)>0)
                            outPutRowNum.add(rowIndex);//符合条件的行数
                    }
                    //output
                    for (int i=0;i<table_columns.size();i++) {
                        System.out.print(table_columns.get(i).getColumnName());
                        System.out.print("  ");
                    }
                    System.out.print("\n");

                    for (int rowIndex=0;rowIndex<outPutRowNum.size();rowIndex++){
                        for (int colIndex=0;colIndex<all_values.get(outPutRowNum.get(rowIndex)).size();colIndex++){
                            System.out.print(all_values.get(outPutRowNum.get(rowIndex)).get(colIndex));
                            System.out.print("  ");
                        }
                        System.out.print("\n");
                    }
                }
                else{
                    //指定列输出
                    for (int x=0,j=0,index=0;x<table_columns.size()&&j<selectItemStrings.size();x++){
                        if(table_columns.get(x).getColumnName().equals(selectItemStrings.get(j))){
                            indexList.add(x);
                            j++;
                            index++;
                        }
                    }
                    if(indexList.size()!=selectItemStrings.size()){
                        //TODO:指明不存在的列名
                        System.out.println("有查询列不存在于表中");
                        Logging.log("有查询列不存在于表中");
                        return;
                    }

                    List<Integer> outPutRowNum = new ArrayList<>();
                    int targetColIndex;
                    for(targetColIndex=0;targetColIndex<table_columns.size();targetColIndex++){
                        if(table_columns.get(targetColIndex).getColumnName().equals(beforeOperator))
                            break;
                    }

                    if(targetColIndex==table_columns.size()){
                        //TODO:指明不存在的列名
                        System.out.println("where后有查询列不存在于表中");
                        Logging.log("where后有查询列不存在于表中");
                        return;
                    }

                    for (int rowIndex=0;rowIndex<all_values.size();rowIndex++){
                        String value=all_values.get(rowIndex).get(targetColIndex).toString().toUpperCase();
                        String compareValue=value.replace("'", "");
                        if(compareValue.compareTo(afterOperator)>0)
                            outPutRowNum.add(rowIndex);//符合条件的行数
                    }

                    //output
                    for (int i=0;i<indexList.size();i++) {
                        System.out.print(table_columns.get(indexList.get(i)).getColumnName());
                        System.out.print("  ");
                    }
                    System.out.print("\n");
                    for (int rowIndex=0;rowIndex<outPutRowNum.size();rowIndex++){
                        for (int colIndex=0;colIndex<indexList.size();colIndex++){
                            System.out.print(all_values.get(outPutRowNum.get(rowIndex)).get(indexList.get(colIndex)));
                            System.out.print("  ");
                        }
                        System.out.print("\n");
                    }

                }

            } else if (operator.equals(">=")) {
                if(selectItemStrings.get(0).equals("*")){
                    List<Integer> outPutRowNum = new ArrayList<>();
                    int targetColIndex;
                    for(targetColIndex=0;targetColIndex<table_columns.size();targetColIndex++){
                        if(table_columns.get(targetColIndex).getColumnName().equals(beforeOperator))
                            break;
                    }

                    if(targetColIndex==table_columns.size()){
                        //TODO:指明不存在的列名
                        System.out.println("where后有查询列不存在于表中");
                        Logging.log("where后有查询列不存在于表中");
                        return;
                    }

                    for (int rowIndex=0;rowIndex<all_values.size();rowIndex++){
                        //不同处
                        String value=all_values.get(rowIndex).get(targetColIndex).toString().toUpperCase();
                        String compareValue=value.replace("'", "");
                        if(compareValue.compareTo(afterOperator)>0)
                            outPutRowNum.add(rowIndex);//符合条件的行数
                        else if (compareValue.compareTo(afterOperator)==0) {
                            outPutRowNum.add(rowIndex);
                        }
                    }
                    //output
                    for (int i=0;i<table_columns.size();i++) {
                        System.out.print(table_columns.get(i).getColumnName());
                        System.out.print("  ");
                    }
                    System.out.print("\n");

                    for (int rowIndex=0;rowIndex<outPutRowNum.size();rowIndex++){
                        for (int colIndex=0;colIndex<all_values.get(outPutRowNum.get(rowIndex)).size();colIndex++){
                            System.out.print(all_values.get(outPutRowNum.get(rowIndex)).get(colIndex));
                            System.out.print("  ");
                        }
                        System.out.print("\n");
                    }
                }
                else{
                    //指定列输出
                    for (int x=0,j=0,index=0;x<table_columns.size()&&j<selectItemStrings.size();x++){
                        if(table_columns.get(x).getColumnName().equals(selectItemStrings.get(j))){
                            indexList.add(x);
                            j++;
                            index++;
                        }
                    }
                    if(indexList.size()!=selectItemStrings.size()){
                        //TODO:指明不存在的列名
                        System.out.println("有查询列不存在于表中");
                        Logging.log("有查询列不存在于表中");
                        return;
                    }

                    List<Integer> outPutRowNum = new ArrayList<>();
                    int targetColIndex;
                    for(targetColIndex=0;targetColIndex<table_columns.size();targetColIndex++){
                        if(table_columns.get(targetColIndex).getColumnName().equals(beforeOperator))
                            break;
                    }

                    if(targetColIndex==table_columns.size()){
                        //TODO:指明不存在的列名
                        System.out.println("where后有查询列不存在于表中");
                        Logging.log("where后有查询列不存在于表中");
                        return;
                    }

                    for (int rowIndex=0;rowIndex<all_values.size();rowIndex++){
                        String value=all_values.get(rowIndex).get(targetColIndex).toString().toUpperCase();
                        String compareValue=value.replace("'", "");
                        if(compareValue.compareTo(afterOperator)>0)
                            outPutRowNum.add(rowIndex);//符合条件的行数
                        else if (compareValue.compareTo(afterOperator)==0) {
                            outPutRowNum.add(rowIndex);
                        }
                    }

                    //output
                    for (int i=0;i<indexList.size();i++) {
                        System.out.print(table_columns.get(indexList.get(i)).getColumnName());
                        System.out.print("  ");
                    }
                    System.out.print("\n");
                    for (int rowIndex=0;rowIndex<outPutRowNum.size();rowIndex++){
                        for (int colIndex=0;colIndex<indexList.size();colIndex++){
                            System.out.print(all_values.get(outPutRowNum.get(rowIndex)).get(indexList.get(colIndex)));
                            System.out.print("  ");
                        }
                        System.out.print("\n");
                    }

                }
            } else if (operator.equals("<")) {
                if(selectItemStrings.get(0).equals("*")){
                    List<Integer> outPutRowNum = new ArrayList<>();
                    int targetColIndex;
                    for(targetColIndex=0;targetColIndex<table_columns.size();targetColIndex++){
                        if(table_columns.get(targetColIndex).getColumnName().equals(beforeOperator))
                            break;
                    }

                    if(targetColIndex==table_columns.size()){
                        //TODO:指明不存在的列名
                        System.out.println("where后有查询列不存在于表中");
                        Logging.log("where后有查询列不存在于表中");
                        return;
                    }

                    for (int rowIndex=0;rowIndex<all_values.size();rowIndex++){
                        //不同处
                        String value=all_values.get(rowIndex).get(targetColIndex).toString().toUpperCase();
                        String compareValue=value.replace("'", "");
                        if(compareValue.compareTo(afterOperator)<0)
                            outPutRowNum.add(rowIndex);//符合条件的行数
                    }
                    //output
                    for (int i=0;i<table_columns.size();i++) {
                        System.out.print(table_columns.get(i).getColumnName());
                        System.out.print("  ");
                    }
                    System.out.print("\n");

                    for (int rowIndex=0;rowIndex<outPutRowNum.size();rowIndex++){
                        for (int colIndex=0;colIndex<all_values.get(outPutRowNum.get(rowIndex)).size();colIndex++){
                            System.out.print(all_values.get(outPutRowNum.get(rowIndex)).get(colIndex));
                            System.out.print("  ");
                        }
                        System.out.print("\n");
                    }
                }
                else{
                    //指定列输出
                    for (int x=0,j=0,index=0;x<table_columns.size()&&j<selectItemStrings.size();x++){
                        if(table_columns.get(x).getColumnName().equals(selectItemStrings.get(j))){
                            indexList.add(x);
                            j++;
                            index++;
                        }
                    }
                    if(indexList.size()!=selectItemStrings.size()){
                        //TODO:指明不存在的列名
                        System.out.println("有查询列不存在于表中");
                        Logging.log("有查询列不存在于表中");
                        return;
                    }

                    List<Integer> outPutRowNum = new ArrayList<>();
                    int targetColIndex;
                    for(targetColIndex=0;targetColIndex<table_columns.size();targetColIndex++){
                        if(table_columns.get(targetColIndex).getColumnName().equals(beforeOperator))
                            break;
                    }

                    if(targetColIndex==table_columns.size()){
                        //TODO:指明不存在的列名
                        System.out.println("where后有查询列不存在于表中");
                        Logging.log("where后有查询列不存在于表中");
                        return;
                    }

                    for (int rowIndex=0;rowIndex<all_values.size();rowIndex++){
                        String value=all_values.get(rowIndex).get(targetColIndex).toString().toUpperCase();
                        String compareValue=value.replace("'", "");
                        if(compareValue.compareTo(afterOperator)<0)
                            outPutRowNum.add(rowIndex);//符合条件的行数
                    }

                    //output
                    for (int i=0;i<indexList.size();i++) {
                        System.out.print(table_columns.get(indexList.get(i)).getColumnName());
                        System.out.print("  ");
                    }
                    System.out.print("\n");
                    for (int rowIndex=0;rowIndex<outPutRowNum.size();rowIndex++){
                        for (int colIndex=0;colIndex<indexList.size();colIndex++){
                            System.out.print(all_values.get(outPutRowNum.get(rowIndex)).get(indexList.get(colIndex)));
                            System.out.print("  ");
                        }
                        System.out.print("\n");
                    }

                }

            } else if (operator.equals("<=")) {
                if(selectItemStrings.get(0).equals("*")){
                    List<Integer> outPutRowNum = new ArrayList<>();
                    int targetColIndex;
                    for(targetColIndex=0;targetColIndex<table_columns.size();targetColIndex++){
                        if(table_columns.get(targetColIndex).getColumnName().equals(beforeOperator))
                            break;
                    }

                    if(targetColIndex==table_columns.size()){
                        //TODO:指明不存在的列名
                        System.out.println("where后有查询列不存在于表中");
                        Logging.log("where后有查询列不存在于表中");
                        return;
                    }

                    for (int rowIndex=0;rowIndex<all_values.size();rowIndex++){
                        //不同处
                        String value=all_values.get(rowIndex).get(targetColIndex).toString().toUpperCase();
                        String compareValue=value.replace("'", "");
                        if(compareValue.compareTo(afterOperator)<0)
                            outPutRowNum.add(rowIndex);//符合条件的行数
                        else if (compareValue.compareTo(afterOperator)==0) {
                            outPutRowNum.add(rowIndex);
                        }
                    }
                    //output
                    for (int i=0;i<table_columns.size();i++) {
                        System.out.print(table_columns.get(i).getColumnName());
                        System.out.print("  ");
                    }
                    System.out.print("\n");

                    for (int rowIndex=0;rowIndex<outPutRowNum.size();rowIndex++){
                        for (int colIndex=0;colIndex<all_values.get(outPutRowNum.get(rowIndex)).size();colIndex++){
                            System.out.print(all_values.get(outPutRowNum.get(rowIndex)).get(colIndex));
                            System.out.print("  ");
                        }
                        System.out.print("\n");
                    }
                }
                else{
                    //指定列输出
                    for (int x=0,j=0,index=0;x<table_columns.size()&&j<selectItemStrings.size();x++){
                        if(table_columns.get(x).getColumnName().equals(selectItemStrings.get(j))){
                            indexList.add(x);
                            j++;
                            index++;
                        }
                    }
                    if(indexList.size()!=selectItemStrings.size()){
                        //TODO:指明不存在的列名
                        System.out.println("有查询列不存在于表中");
                        Logging.log("有查询列不存在于表中");
                        return;
                    }

                    List<Integer> outPutRowNum = new ArrayList<>();
                    int targetColIndex;
                    for(targetColIndex=0;targetColIndex<table_columns.size();targetColIndex++){
                        if(table_columns.get(targetColIndex).getColumnName().equals(beforeOperator))
                            break;
                    }

                    if(targetColIndex==table_columns.size()){
                        //TODO:指明不存在的列名
                        System.out.println("where后有查询列不存在于表中");
                        Logging.log("where后有查询列不存在于表中");
                        return;
                    }

                    for (int rowIndex=0;rowIndex<all_values.size();rowIndex++){
                        String value=all_values.get(rowIndex).get(targetColIndex).toString().toUpperCase();
                        String compareValue=value.replace("'", "");
                        if(compareValue.compareTo(afterOperator)<0)
                            outPutRowNum.add(rowIndex);//符合条件的行数
                        else if (compareValue.compareTo(afterOperator)==0) {
                            outPutRowNum.add(rowIndex);
                        }
                    }

                    //output
                    for (int i=0;i<indexList.size();i++) {
                        System.out.print(table_columns.get(indexList.get(i)).getColumnName());
                        System.out.print("  ");
                    }
                    System.out.print("\n");
                    for (int rowIndex=0;rowIndex<outPutRowNum.size();rowIndex++){
                        for (int colIndex=0;colIndex<indexList.size();colIndex++){
                            System.out.print(all_values.get(outPutRowNum.get(rowIndex)).get(indexList.get(colIndex)));
                            System.out.print("  ");
                        }
                        System.out.print("\n");
                    }

                }
            }
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

