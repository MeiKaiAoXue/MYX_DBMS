package org.myx;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Login {

    private JFrame frame;
    private JButton createDB;
    private JButton selectDBButton;
    private JButton deleteDBButton;
    private JPanel loginPanel;

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
    }

    public static void main(String[] args) {
        Login login = new Login();
        login.frame = new JFrame("Login");
        login.frame.setContentPane(login.loginPanel);
        login.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        login.frame.pack();
        login.frame.setLocationRelativeTo(null); // 窗口居中
        login.frame.setVisible(true);
    }

}
