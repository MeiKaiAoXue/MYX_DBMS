package org.myx;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static java.lang.System.exit;
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
                    String filePath = "./" + dbName + ".txt";
                    initDB(filePath);
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
