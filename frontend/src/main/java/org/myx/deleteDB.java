package org.myx;

import org.myx.fileIo.FileUtils;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

public class deleteDB {

    public JFrame frame;
    public JPanel deletePanel;
    private JButton deleteButton;
    private ButtonGroup group;

    public deleteDB(){
        List<String> dbNames = FileUtils.loadDBNames();
        group = new ButtonGroup();
        for (String dbName : dbNames){
            JRadioButton radioButton = new JRadioButton(dbName);
            radioButton.setActionCommand(dbName);
            group.add(radioButton);
            deletePanel.add(radioButton);
        }

        deleteButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedDB = group.getSelection().getActionCommand();
                FileUtils.deleteDB(selectedDB);
                JOptionPane.showMessageDialog(frame, "数据库删除成功", "删除成功", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
            }
        });
    }
}
