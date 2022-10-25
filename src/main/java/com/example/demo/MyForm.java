package com.example.demo;


import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

public class MyForm extends JFrame {
    private JButton button1;
    private JTable table1;
    private JPanel myPanel;

    public MyForm(String title) throws HeadlessException {
        super(title);

        button1.addActionListener(this::onClick);
        table1.getTableHeader().setVisible(true);
        table1.getTableHeader().setColumnModel(new MyHeaderModel("FirstName", "SecondName", "Score"));

    }

    private void onClick(ActionEvent e) {
        java.util.List<MyData> myDataList = Arrays.asList(
                new MyData("first1", "second1", randomScore()),
                new MyData("first2", "second2", randomScore()),
                new MyData("first3", "second3", randomScore()));

         table1.setModel(new MyTableModel(myDataList));
        System.out.println("clicked");

    }

    private int randomScore() {
        Double v = Math.random() * 100;
        return v.intValue();
    }

    public JPanel getMyPanel() {
        return myPanel;
    }
}
