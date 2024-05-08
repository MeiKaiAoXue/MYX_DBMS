package org.myx;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class Form {
    private JPanel mainWindow;
    private JLabel title;
    private JTextArea SQLTextArea;
    private JTextArea resultTextArea;
    private JButton exeButton;
    private JTextArea consoleArea;
    private JTextArea SQLtextArea;
    private JTextArea resultArea;
    private JButton commit;

    public Form(JFrame frame){
        //JTextArea自动换行
        SQLTextArea.setLineWrap(true);
        SQLTextArea.setWrapStyleWord(true);
        //执行
        exeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
