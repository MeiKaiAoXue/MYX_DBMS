package org.myx;

import org.myx.fileIo.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class deleteDB {

    public JFrame frame;
    public JPanel deletePanel;
    private JButton deleteButton;
    private JPanel list;
    private ButtonGroup group;

    public deleteDB(){
        List<String> dbNames = FileUtils.loadDBNames();
        group = new ButtonGroup();
        for (String dbName : dbNames){
            JRadioButton radioButton = new JRadioButton(dbName);
            radioButton.setActionCommand(dbName);
            Font newFont = new Font("Serif", Font.BOLD, 18); // 选择字体名称、样式和大小
            radioButton.setFont(newFont);
            group.add(radioButton);
            list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
            list.add(radioButton);
            list.add(Box.createVerticalStrut(10));
        }

        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedDB = group.getSelection().getActionCommand();
                FileUtils.deleteDB(selectedDB);
                FileUtils.updateDBList(selectedDB);
                JOptionPane.showMessageDialog(frame, "数据库删除成功", "删除成功", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            }
        });

    }
}
