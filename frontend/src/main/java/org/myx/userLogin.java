package org.myx;

import org.myx.fileIo.FileUtils;
import org.myx.fileIo.metadata.UserMetaData;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class userLogin {
    public JFrame frame;
    public JPanel userLoginPanel;
    private JTextField countFiled;
    private JTextField passwordField;
    private JButton loginButton;
    public userLogin(){

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try{
                    if(authenticate(countFiled.getText(),passwordField.getText())){
                       openForm();
                    }else{
                        JOptionPane.showMessageDialog(frame,"用户名或者密码错误");
                    }
                }catch(IOException | ClassNotFoundException ex){
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame,"无法获取用户数据");
                }
            }
            private boolean authenticate(String username, String password) throws IOException, ClassCastException, ClassNotFoundException {
                try {
                    // 尝试从文件中读取对象
                    Object result = FileUtils.readObjectFromFile("./users.txt");

                    // 检查返回的对象是否为List<UserMetaData>类型
                    if (result instanceof List<?>) {
                        List<?> list = (List<?>) result;
                        if (!list.isEmpty() && list.get(0) instanceof UserMetaData) {
                            List<UserMetaData> users = (List<UserMetaData>) list;

                            // 遍历用户列表，检查每个用户
                            for (UserMetaData user : users) {
                                if (user.getUserName().equals(username) && user.getPassword().equals(password)) {
                                    return true;  // 找到匹配的用户，验证成功
                                }
                            }
                        }
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();  // 打印异常信息
                    System.out.println("验证过程中出错：" + e.getMessage());
                }
                return false;  // 没有找到匹配的用户，验证失败
            }
            private void openForm() {
                Form form = new Form();
                form.frame = new JFrame("Form");
                form.frame.setContentPane(form.formPanel);
                form.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                form.frame.pack();
                form.frame.setLocationRelativeTo(null);
                form.frame.setVisible(true);
            }
        });
    }
}