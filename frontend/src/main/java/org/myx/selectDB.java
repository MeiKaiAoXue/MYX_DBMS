package org.myx;

import org.myx.fileIo.FileUtils;
import org.myx.processing.Processor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class selectDB {
    public JFrame frame;
    public JPanel selectPanel;
    private JButton selected;
    private ButtonGroup group;

    public selectDB() {
        List<String> dbNames = FileUtils.loadDBNames();
        group = new ButtonGroup();
        for (String dbName : dbNames){
            JRadioButton radioButton = new JRadioButton(dbName);
            radioButton.setActionCommand(dbName);
            group.add(radioButton);
            selectPanel.add(radioButton);
        }
        selected.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String selectedDB = group.getSelection().getActionCommand();
                System.out.println("选中了数据库:" + selectedDB);
                Processor.setCurrentDBName(selectedDB);
                openForm();
                frame.dispose();
            }
        });
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
}
