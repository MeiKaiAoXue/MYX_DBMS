//import org.myx.fileIo.FileUtils;
//import org.myx.fileIo.metadata.DBMetaData;
//import org.myx.fileIo.metadata.TableMetaData1;
//import org.myx.processing.Processor;
//
//import java.util.List;
//
//public class TestSelect {
//    public static void main(String[] args) {
//        FileUtils.initDB("./db.txt");
//        String create_sql3 = "CREATE TABLE AA (column1 INT CHECK (column1 > 0), column2 VARCHAR(20) NOT NULL UNIQUE);";
//        Processor.process(create_sql3);
//        DBMetaData db = (DBMetaData) FileUtils.readObjectFromFile("db.txt");
//        for (TableMetaData1 table : db.getTables()) {
//            System.out.println(table.getTableName());
//            List<TableMetaData1.ColumnMetaData> columns = table.getColumns();
//            for (TableMetaData1.ColumnMetaData column : columns) {
//                System.out.println(column.getColumnName() + " " + column.getColumnType() + " ");
//            }
//        }
//        String insert_sql3 = "INSERT INTO AA (column1, column2) VALUES (1, 'hello')";
//        String insert_sql4 = "INSERT INTO AA (column1, column2) VALUES (2, 'bye')";
//        String insert_sql5 = "INSERT INTO AA (column1, column2) VALUES (3, 'ciao')";
//        String insert_sql6 = "INSERT INTO AA (column1, column2) VALUES (4, 'hello1')";
//        Processor.process(insert_sql3);
//        Processor.process(insert_sql4);
//        Processor.process(insert_sql5);
//        Processor.process(insert_sql6);
//        //String sqlSelect="select * from AA";    //通过
//        //String sqlSelect="select column1,column2 from AA";    //通过
//        //String sqlSelect="select column2 from AA";    //通过
//        //String sqlSelect="select * from AA where column1 = '1'"; //通过
//        //String sqlSelect="select column2 from AA where column1 = '3'";
//        //String sqlSelect="select column2 from AA where column1 < '3'";// 通过
//        //String sqlSelect="select * from AA where column1 < '3'";// 通过
//        //String sqlSelect="select column1,column2 from AA where column1 < '3'";// 通过
//        //String sqlSelect="select * from AA where column1 >= '2'";// 通过
//        //String sqlSelect="select column1,column2 from AA where column1 <= '2'";
//        String sqlSelect="select column1,column2 from AA where column2 <= 'ciao'";
//        Processor.process(sqlSelect);
//    }
//}
//
//
