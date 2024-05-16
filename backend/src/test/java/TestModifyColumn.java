import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterExpression;
import org.myx.fileIo.FileUtils;
import org.myx.fileIo.metadata.DBMetaData;
import org.myx.fileIo.metadata.TableMetaData1;
import org.myx.processing.Processor;

import java.util.List;

public class TestModifyColumn {
    public void testModifyColumn() {


        try {
            // create table
            FileUtils.initDB("./db.txt");
            String createTableSql = "CREATE Table BB (column1 INT CHECK (column1 > 2) );";
            Processor.process(createTableSql);
            DBMetaData db = (DBMetaData) FileUtils.readObjectFromFile("db.txt");
            for (TableMetaData1 table : db.getTables()) {
                System.out.println(table.getTableName());
                List<TableMetaData1.ColumnMetaData> columns = table.getColumns();
                for (TableMetaData1.ColumnMetaData column : columns) {
                    System.out.println(column.getColumnName() + " " + column.getColumnType() + " ");
                }
            }

            // insert data
            String insertDataSql1 = "INSERT INTO BB (column1) VALUES (3);";
            String insertDataSql2 = "INSERT INTO BB (column1) VALUES (1);";
            Processor.process(insertDataSql1);
            Processor.process(insertDataSql2);

            // modify column
            String modifyColumnSql = "ALTER TABLE BB MODIFY COLUMN column1 VARCHAR(20);";
            String modifyColumnSql2 = "ALTER TABLE BB ADD CONSTRAINT CK_column1 CHECK (column1 > 0);";
            String modifyColumnSql3 = "ALTER TABLE BB DROP CONSTRAINT CK_column1;";
            Processor.process(modifyColumnSql);
            Processor.process(modifyColumnSql2);
            Processor.process(modifyColumnSql3);
        } catch (Exception e) {
            e.printStackTrace();
        }






//            String sql = "ALTER TABLE employees\n" +
//                    "DROP CONSTRAINT emp_email_uk;";
//            String sql2 = "ALTER TABLE employees\n" +
//                    "ADD CONSTRAINT emp_email_uk UNIQUE (email);";
//            String sql3 = "ALTER TABLE employees\n" +
//                    "MODIFY COLUMN email VARCHAR(100);";
//            Alter alter = (Alter) CCJSqlParserUtil.parse(sql3);
//
//            // 获取表名
//            String tableName = alter.getTable().getName();
//            System.out.println("Table Name: " + tableName);
//
//            // 获取修改的表达式
//            for (AlterExpression alterExpression : alter.getAlterExpressions()) {
//                System.out.println("Alter Expression: " + alterExpression.toString());
//                System.out.println("getUK: " + alterExpression.getUk());
//                System.out.println("getConstraints: " + alterExpression.getConstraints());
//                System.out.println("getColumnName: " + alterExpression.getColumnName());
//                System.out.println("getParameters: " + alterExpression.getParameters());
//
//            }

//            Processor.process(sql2);
//            Processor.process(sql);
//            Processor.process(sql3);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
