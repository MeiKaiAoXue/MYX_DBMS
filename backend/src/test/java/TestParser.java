//import net.sf.jsqlparser.JSQLParserException;
//import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
//import net.sf.jsqlparser.parser.CCJSqlParserUtil;
//import net.sf.jsqlparser.schema.Column;
//import net.sf.jsqlparser.schema.Table;
//import net.sf.jsqlparser.statement.Statement;
//import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
//import net.sf.jsqlparser.statement.create.table.CreateTable;
//import net.sf.jsqlparser.statement.create.table.ForeignKeyIndex;
//import net.sf.jsqlparser.statement.create.table.Index;
//import net.sf.jsqlparser.statement.delete.Delete;
//import net.sf.jsqlparser.statement.select.*;
//import net.sf.jsqlparser.util.TablesNamesFinder;
//import net.sf.jsqlparser.util.validation.Validation;
//
//import java.util.List;
//import java.util.Set;
//
//public class TestParser {
////    public void testParse() throws JSQLParserException {
////        String sqlStr = "select aa,1,2,3 from dual,start where EXISTS (select * from dual1 where a=b) and a=1";
////        String sqlStr2 = "delete from dual where a=b , CONSTRAINT pk_a1 PRIMARY KEY (a, b)";
////        String sqlStr3 = "CREATE TABLE a1 ( a NUMBER PRIMARY KEY, b NUMBER NOT NULL UNIQUE, c NUMBER DEFAULT 1, d NUMBER REFERENCES a2(A), e NUMBER, CONSTRAINT ck_d_positive CHECK (d > 0) ,CONSTRAINT uk_a1_b UNIQUE (b));";
////        String sqlStr4 = "CREATE TABLE AA (column1 INT, column2 VARCHAR(255));";
////        Statement statement = CCJSqlParserUtil.parse(sqlStr3);
////        if (statement instanceof CreateTable createTable) {
////            System.out.println(createTable.getTable().getName());
////            System.out.println(createTable.getColumnDefinitions());
////            for (ColumnDefinition columnDefinition : createTable.getColumnDefinitions()) {
////                System.out.println("Column definition:" + columnDefinition.toString());
////                System.out.println("Name: " + columnDefinition.getColumnName());
////                System.out.println("Type: " + columnDefinition.getColDataType().getDataType());
////                System.out.println("Constraints: " + columnDefinition.getColumnSpecs());
////                if (columnDefinition.getColumnSpecs() != null) {
////                    for (Object columnSpec : columnDefinition.getColumnSpecs()) {
////                        System.out.println("Column spec: " + columnSpec);
////                    }
////                }
////            }
////            List<Index> indexes = createTable.getIndexes();
////            if (indexes != null) {
////                for (Index index : indexes) {
////                    System.out.println("Type: " + index.getType());
////                    if ("FOREIGN KEY".equals(index.getType())) {
////                        System.out.println("Foreign key constraint:");
////                        System.out.println("Name: " + index.getName());
////                        System.out.println("Columns: " + index.getColumnsNames());
////                        if (index instanceof ForeignKeyIndex) {
////                            ForeignKeyIndex foreignKeyIndex = (ForeignKeyIndex) index;
////                            System.out.println("Referenced table: " + foreignKeyIndex.getTable().getName());
////                        }
////                    }
////                    if ("PRIMARY KEY".equals(index.getType())) {
////                        System.out.println("Composite primary key:");
////                        System.out.println("Name: " + index.getName());
////                        System.out.println("Columns: " + index.getColumnsNames());
////                    }
////                    if ("UNIQUE".equals(index.getType())) {
////                        System.out.println("UNIQUE:");
////                        System.out.println("Name: " + index.getName());
////                        System.out.println("Columns: " + index.getColumnsNames());
////                    }
////                }
////
////
////            }
////        }
//
//
////        Select select =  (Select) CCJSqlParserUtil.parse(sqlStr);
////        PlainSelect plainSelect = (PlainSelect) select;
////        System.out.println(plainSelect.getWhere());
//
////        if (select instanceof Select)
////        {
////            System.out.println("select");
////            System.out.println(TablesNamesFinder.findTables(sqlStr));
////        }
////        else if (select instanceof Delete)
////        {
////            System.out.println("delete");
////            System.out.println(TablesNamesFinder.findTables(sqlStr2));
////
////        }
////        else
////        {
////            System.out.println("other");
////        }
//////        List<SelectItem<?>> selectItem =
//////                select.getSelectItems();
//////        for (SelectItem<?> item : selectItem) {
//////            System.out.println(item.toString());
//////        }
//
//
////        Table table = (Table) select.getFromItem();
////        System.out.println(table.getName());
//
//
////        FromItem fromItem = select.getFromItem();
////
////        EqualsTo equalsTo = (EqualsTo) select.getWhere();
////        Column a = (Column) equalsTo.getLeftExpression();
////        Column b = (Column) equalsTo.getRightExpression();
////        System.out.println(a.getColumnName());
////        System.out.println(b.getColumnName());
////        }
////    }
//}
