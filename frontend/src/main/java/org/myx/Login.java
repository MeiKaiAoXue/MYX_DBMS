package org.myx;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.ui.FlatListCellBorder;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login {

    public JFrame frame;
    private JButton createDB;
    private JButton selectDBButton;
    private JButton deleteDBButton;
    public JPanel loginPanel;

    public Login() {
        createDB.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("点击了createDB");
                //创建createDB对象
                createDB createDB1 = new createDB();
                createDB1.frame = new JFrame("创建数据库");
                createDB1.frame.setContentPane(createDB1.createPanel);
                createDB1.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                createDB1.frame.pack();
                createDB1.frame.setLocationRelativeTo(null); // 窗口居中
                createDB1.frame.setVisible(true); // 显示窗口
            }
        });
        deleteDBButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("点击了deleteDB");
                //创建删除数据库对象
                deleteDB deleteDB = new deleteDB();
                deleteDB.frame = new JFrame("删除数据库");
                deleteDB.frame.setContentPane(deleteDB.deletePanel);
                deleteDB.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                deleteDB.frame.pack();
                deleteDB.frame.setLocationRelativeTo(null); // 窗口居中
                deleteDB.frame.setVisible(true); // 显示窗口

            }
        });
        selectDBButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("点击了selectDB");
                //创建选择数据库对象
                selectDB selectDB = new selectDB();
                selectDB.frame = new JFrame("选择数据库");
                selectDB.frame.setContentPane(selectDB.selectPanel);
                selectDB.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                selectDB.frame.pack();
                selectDB.frame.setLocationRelativeTo(null);
                selectDB.frame.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        FlatLightLaf.setup();
        Login login = new Login();
        login.frame = new JFrame("Login");
        login.frame.setContentPane(login.loginPanel);
        login.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        login.frame.pack();
        login.frame.setLocationRelativeTo(null); // 窗口居中
        login.frame.setVisible(true);
    }

}
