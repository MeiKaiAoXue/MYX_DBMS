package org.myx;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.grant.Grant;
import net.sf.jsqlparser.statement.select.Select;
import org.myx.fileIo.FileUtils;
import org.myx.fileIo.TextOutputStream;
import org.myx.fileIo.metadata.DBMetaData;
import org.myx.fileIo.metadata.TableMetaData1;
import org.myx.fileIo.metadata.UserMetaData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import static org.myx.processing.Processor.process;
import static org.myx.processing.Processor.*;

public class Form {
    public JFrame frame;
    public JPanel formPanel;
    private JTextArea SQLtextArea;
    private JTextArea resultArea;
    private JButton commit;
    private JPanel tablePanel;
    private JLabel userLabel;
    private JTree tree1;
    private UserMetaData user;

    public Form(UserMetaData user){
        this.user = user;
        userLabel.setText("当前用户：" + user.getUserName());
        String privilege = user.getUserPrivilege();
        SQLtextArea.setLineWrap(true);
        SQLtextArea.setWrapStyleWord(true);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        commit.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                // 重定向 System.out 输出到 JTextArea
                TextOutputStream taOutputStream = new TextOutputStream(resultArea);
                PrintStream ps = new PrintStream(taOutputStream);
                System.setOut(ps);  // 将标准输出重定向到 PrintStream
                System.out.println("Executing SQL: " + SQLtextArea.getText());
                String sql = SQLtextArea.getText();
                try {
                    Statement statement = CCJSqlParserUtil.parse(sql.toUpperCase());
                    if(statement instanceof CreateTable && (privilege == "connect")){
                        System.out.println("当前用户没有创建表格的权限！");
                    }else if(statement instanceof Grant && (privilege!="dba")){
                        System.out.println("当前用户没有授予权限和撤销权限的功能！");
                    }else if(statement instanceof Select){
                        List<String> tables = processSelect((Select) statement);
                        // 处理列名
                        Vector<String> columnNames = new Vector<>();
                        columnNames.addAll(Arrays.asList(tables.get(0).split(" ")));
                        // 处理数据
                        Vector<Vector<Object>> data = new Vector<>();
                        for (int i = 1; i < tables.size(); i++) {
                            Vector<Object> row = new Vector<>();
                            String[] rowData = tables.get(i).split(" ");
                            for (String cell : rowData) {
                                row.add(cell);
                            }
                            data.add(row);
                        }
                        try{
                            // 创建表格模型
                            DefaultTableModel model = new DefaultTableModel(data, columnNames);
                            JTable table = new JTable(model);
                            table.setFont(new Font("",Font.BOLD,19));
                            JScrollPane scrollPane = new JScrollPane(table); // 将表格添加到滚动窗格中
                            tablePanel.removeAll(); // 移除旧组件
                            tablePanel.add(scrollPane, BorderLayout.CENTER); // 将滚动窗格添加到面板中
                            tablePanel.revalidate(); // 通知布局管理器更新布局
                            tablePanel.repaint(); // 重新绘制面板
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    else {
                        process(sql);
                    }
                } catch (JSQLParserException | IOException ex) {
                    throw new RuntimeException(ex);
                }
                //if(statement instanceof CreateTable && (privilege=="dba"||privilege=="resource"))

            }
        });
    }

    private void createNodes(DefaultMutableTreeNode top){
    }
}
