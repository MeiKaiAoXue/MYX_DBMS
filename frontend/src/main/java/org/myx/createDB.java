package org.myx;

import org.myx.fileIo.FileUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import static java.lang.System.exit;
import static org.myx.fileIo.FileUtils.initAdminUser;
import static org.myx.fileIo.FileUtils.initDB;

public class createDB implements ActionListener{
    private JTextField DBname;
    private JButton Create;

    public JPanel createPanel;

    JFrame frame;

    public createDB(){

        Create.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getSource() == Create){
                    String dbName = DBname.getText();
                    String filePath = "./" + dbName ;
                    try {
                        initDB(filePath);
                        initAdminUser("./" + dbName + "./users.txt","admin","123456");
                        FileUtils.saveDBName(dbName);

                        // 显示创建成功的消息
                        JOptionPane.showMessageDialog(frame, "数据库创建成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

                        // 关闭当前窗口
                        frame.dispose();


                    } catch (Exception err) {
                        // 错误处理：显示错误消息
                        JOptionPane.showMessageDialog(frame, "数据库创建失败：" + err.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    public void setContentPane(JPanel createPanel) {
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }
}
