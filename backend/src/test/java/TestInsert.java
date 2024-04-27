import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Join;
import org.myx.fileIo.FileUtils;
import org.myx.fileIo.metadata.DBMetaData;
import org.myx.fileIo.metadata.TableMetaData1;
import org.myx.processing.Processor;

import java.util.List;

public class TestInsert {
    public void testInsert() {
        String sql = "INSERT INTO table_name ;";
        String sql2 = "INSERT INTO employees (employee_id, first_name, last_name, email, hire_date, job_id, salary) VALUES (101, 'John', 'Doe', 'john.doe@example.com', TO_DATE('2024-04-16', 'YYYY-MM-DD'), 'IT_PROG', 5000);";
        String sql3 = "INSERT INTO employees (employee_id, first_name, last_name, email, hire_date, job_id, salary)\n" +
                "VALUES \n" +
                "    (102, 'Jane', 'Smith', 'jane.smith@example.com', TO_DATE('2024-04-16', 'YYYY-MM-DD'), 'SA_REP', 6000),\n" +
                "    (103, 'Mike', 'Johnson', 'mike.johnson@example.com', TO_DATE('2024-04-17', 'YYYY-MM-DD'), 'IT_PROG', 5500),\n" +
                "    (104, 'Emily', 'Davis', 'emily.davis@example.com', TO_DATE('2024-04-18', 'YYYY-MM-DD'), 'HR_REP', 7000);\n";
        String sql4 = "INSERT INTO employees (employee_id, first_name, last_name, email, hire_date, job_id, salary)\n" +
                "SELECT employee_id, first_name, last_name, email, hire_date, job_id, salary\n" +
                "FROM new_employees\n" +
                "WHERE hire_date > TO_DATE('2024-01-01', 'YYYY-MM-DD');\n";
        String sql5 = "INSERT INTO employees (employee_id, first_name, last_name, email, hire_date, job_id, salary)\n" +
                "SELECT o.order_id, c.customer_name, p.product_name, COUNT(*) as order_count\n" +
                "FROM orders o\n" +
                "JOIN customers c ON o.customer_id = c.customer_id\n" +
                "JOIN products p ON o.product_id = p.product_id\n" +
                "WHERE o.order_date >= '2024-01-01' -- 添加 WHERE 条件\n" +
                "GROUP BY o.order_id, c.customer_name, p.product_name\n" +
                "HAVING order_count > 1 -- 添加 HAVING 条件\n" +
                "ORDER BY o.order_id DESC -- 添加 ORDER BY 子句\n" +
                "LIMIT 10;";
//        parseInsert(sql2);



        // 以下代码用于测试insert功能
        FileUtils.initDB("./db.txt");
        String create_sql3 = "CREATE TABLE AA (column1 INT CHECK (column1 > 0), column2 VARCHAR(20) NOT NULL UNIQUE);";
        Processor.process(create_sql3);
        DBMetaData db = (DBMetaData) FileUtils.readObjectFromFile("db.txt");
        for (TableMetaData1 table : db.getTables()) {
            System.out.println(table.getTableName());
            List<TableMetaData1.ColumnMetaData> columns = table.getColumns();
            for (TableMetaData1.ColumnMetaData column : columns) {
                System.out.println(column.getColumnName() + " " + column.getColumnType() + " ");
            }
        }
//        String insert_sql3 = "INSERT INTO AA (column1, column2) VALUES (1, 'hello');";
        String insert_sql3 = "INSERT INTO AA (column1) VALUES (1);";
//        String insert_sql4 = "INSERT INTO AA (column1, column2) VALUES (2, 'hello');";
        Processor.process(insert_sql3);
//        Processor.process(insert_sql4);
        List<List<Object>> all_values = (List<List<Object>>) FileUtils.readObjectFromFile("AA.txt");
        for (List<Object> values : all_values) {
            for (Object value : values) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
    }


//    public static void parseInsert(String sql) {
//        try {
//            Statement statement = CCJSqlParserUtil.parse(sql);
//            if (statement instanceof Insert) {
//                Insert insertStatement = (Insert) statement;
//                System.out.println("Table: " + insertStatement.getTable().getName());
//                if (insertStatement.getColumns() != null) {
//
//                    System.out.println("Columns: " + insertStatement.getColumns());
//                }
//                if (insertStatement.getSelect() != null) {
//                    System.out.println("Values: " + insertStatement.getSelect());
//                    System.out.println("GetValues: " + insertStatement.getSelect().getValues());
//                    System.out.println("GetValuesGetExpressionsNum: " + insertStatement.getSelect().getValues().getExpressions().size());
//                    System.out.println("GetValuesGetExpressions: " + insertStatement.getSelect().getValues().getExpressions().get(0).toString());
//                    System.out.println("SelectBody: " + insertStatement.getSelect().getSelectBody());
//                    System.out.println("Fetch: " + insertStatement.getSelect().getFetch());
//                    System.out.println("Limit: " + insertStatement.getSelect().getLimit());
//                    System.out.println("OrderByElements: " + insertStatement.getSelect().getOrderByElements());
////                    System.out.println("SetOperationList: " + insertStatement.getSelect().getSetOperationList());
//                    System.out.println("WithItemsList: " + insertStatement.getSelect().getWithItemsList());
//                    System.out.println("plainSelect: " + insertStatement.getSelect().getPlainSelect());
//                    System.out.println("FromItem: " + insertStatement.getSelect().getPlainSelect().getFromItem());
//                    System.out.println("SelectItems: " + insertStatement.getSelect().getPlainSelect().getSelectItems());
//                    System.out.println("Having: " + insertStatement.getSelect().getPlainSelect().getHaving());
//                    System.out.println("Where: " + insertStatement.getSelect().getPlainSelect().getWhere());
//                    System.out.println("First: " + insertStatement.getSelect().getPlainSelect().getFirst());
//                    System.out.println("Joins: " + insertStatement.getSelect().getPlainSelect().getJoins());
//                    for (Join join : insertStatement.getSelect().getPlainSelect().getJoins()) {
//                        System.out.println("Join: " + join);
//                        System.out.println("RightItem: " + join.getRightItem());
//                        System.out.println("FromItem: " + join.getFromItem());
//                        System.out.println("OnExpressions: " + join.getOnExpressions());
//                        System.out.println("OnExpression: " + join.getOnExpression());
//                        System.out.println("UsingColumns: " + join.getUsingColumns());
//                        System.out.println("IsSimple: " + join.isSimple());
//                        System.out.println("IsOuter: " + join.isOuter());
//                        System.out.println("IsRight: " + join.isRight());
//                        System.out.println("IsLeft: " + join.isLeft());
//                        System.out.println("IsNatural: " + join.isNatural());
//                        System.out.println("IsFull: " + join.isFull());
//                        System.out.println("IsInner: " + join.isInner());
//                        System.out.println("IsCross: " + join.isCross());
//                        System.out.println("IsSemi: " + join.isSemi());
//                        System.out.println("IsStraight: " + join.isStraight());
//                    }
//                }
//            } else {
//                System.out.println("Not an INSERT statement");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}