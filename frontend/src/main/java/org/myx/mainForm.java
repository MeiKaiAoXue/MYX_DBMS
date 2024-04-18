import org.myx.*;
import org.myx.fileIo.FileUtils;
import org.myx.fileIo.metadata.DBMetaData;
import org.myx.fileIo.metadata.TableMetaData1;
import org.myx.processing.Processor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class mainForm {
    private JPanel mainWindow;
    private JLabel title;
    private JTextArea SQLTextArea;
    private JTextArea resultTextArea;
    private JButton exeButton;

    public mainForm(JFrame frame){
        //JTextArea自动换行
        SQLTextArea.setLineWrap(true);
        SQLTextArea.setWrapStyleWord(true);
        //执行
        exeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String sql = SQLTextArea.getText();
                //processSql(sql);
                FileUtils.initDB("./db.txt");
                Processor.process(sql);
                DBMetaData dbMetaData = (DBMetaData)FileUtils.readObjectFromFile("./db.txt");
                System.out.println("Length of db: " + dbMetaData.getTables().size());
                for (TableMetaData1 table : dbMetaData.getTables()) {
                    System.out.println("Table Name: " + table.getTableName());
                    for (TableMetaData1.ColumnMetaData column : table.getColumns()) {
                        System.out.println("Column: " + column.getColumnName() + " " + column.getColumnType());
                    }
                    for (TableMetaData1.ConstraintsMetaData constraint : table.getConstraints()) {
                        System.out.println("Constraint: " + constraint.getConstraintName() + " " + constraint.getConstraintType() + " " + constraint.getConstraintCondition());
                    }
                }
            }
        });
    }
}
