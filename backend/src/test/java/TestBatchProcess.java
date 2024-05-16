import org.myx.fileIo.FileUtils;
import org.myx.processing.Processor;

public class TestBatchProcess {
    public void testBatchProcess() {
        FileUtils.initDB("dropTableTest");
        Processor.setCurrentDBName("dropTableTest");
        String create_sql3 = "CREATE TABLE AA (column1 INT CHECK (column1 > 0), column2 VARCHAR(20) NOT NULL UNIQUE);";
        Processor.process(create_sql3);
        System.out.println("我要开始批处理SQL语句啦！！！！！！！！！！！！！！！！！");
        Processor.batchProcess("C:\\Users\\meisiyu\\Desktop\\sql.txt");
    }
}
