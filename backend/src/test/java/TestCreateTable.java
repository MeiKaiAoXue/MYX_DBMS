//import org.myx.fileIo.FileUtils;
//import org.myx.fileIo.metadata.DBMetaData;
//import org.myx.fileIo.metadata.TableMetaData1;
//import org.myx.processing.Processor;
//
//import java.io.StreamCorruptedException;
//import java.util.List;
//
//public class TestCreateTable {
//    public void testCreateTable() {
//        FileUtils.initDB("./db.txt");
//        String sql2 = "Create Table BB (column1 INT CHECK (column1 > 2) );";
//        String sql3 = "CREATE TABLE AA (column1 INT REFERENCES aa(a2), column2 VARCHAR(255) NOT NULL UNIQUE);";
//        Processor.process(sql3);
////        Processor.process(sql2);
//        System.out.println("Create Table Test passed");
//
//        DBMetaData dbMetaData = (DBMetaData)FileUtils.readObjectFromFile("./db.txt");
//        System.out.println("Length of db: " + dbMetaData.getTables().size());
//        for (TableMetaData1 table : dbMetaData.getTables()) {
//            System.out.println("Table Name: " + table.getTableName());
//            for (TableMetaData1.ColumnMetaData column : table.getColumns()) {
//                System.out.println("Column: " + column.getColumnName() + " " + column.getColumnType());
//            }
//            for (TableMetaData1.ConstraintsMetaData constraint : table.getConstraints()) {
//                System.out.println("Constraint: " + constraint.getConstraintName() + " || " + constraint.getConstraintType() + " || " + constraint.getConstraintCondition());
//            }
//        }
//
//        String str = "(COLUMN1 > 2)";
//        str = str.replaceAll("\\((.*?)\\)", "$1");
//        String[] list = str.split(">");
//        if (list.length < 2) {
//            System.out.println("Invalid format: no '>' character found");
//        } else {
//            try {
//                int num = Integer.parseInt(list[1].trim());
//                System.out.println("inside: " + str);
//                System.out.println("num: " + num);
//                System.out.println("list: " + list[1]);
//            } catch (NumberFormatException e) {
//                System.out.println("Invalid format: cannot parse '" + list[1] + "' as an integer");
//            }
//        }
//
//
//
//    }
//
////    public static void main(String[] args) {
////        TestCreateTable test = new TestCreateTable();
////        test.testCreateTable();
////
////    }
//}
