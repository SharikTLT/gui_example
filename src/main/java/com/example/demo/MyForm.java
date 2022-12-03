package com.example.demo;


import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Arrays;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

public class MyForm extends JFrame {
    private JButton button1;
    private JTable table1;
    private JPanel myPanel;
    private JComboBox comboBox1;

    public MyForm(String title) throws HeadlessException {
        super(title);

        MyCell myCell = new MyCell();

        Container parent = table1.getParent();
        parent.remove(table1);

        URL urlToOpen = ProtocolUrlHolder.getInstance().getUnused();
        System.out.println("First url = "+urlToOpen);

        urlToOpen = ProtocolUrlHolder.getInstance().getUnused();
        System.out.println("Second url = "+urlToOpen);

        table1 = new JTable() {

            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 3) {
                    return myCell;
                }
                return super.getCellRenderer(row, column);
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 3) {
                    return myCell;
                }
                return super.getCellEditor(row, column);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 3) {
                    return true;
                }
                return super.isCellEditable(row, column);
            }
        };

//        table1.addMouseListener(myCell);
        parent.add(table1);

        button1.addActionListener(this::onClick);
        table1.getTableHeader().setVisible(true);
        table1.getTableHeader().setColumnModel(new MyHeaderModel("FirstName", "SecondName", "Score", "Action"));


        Map<Integer, String> map = new HashMap<>();

        map.put(1, "Один");
        map.put(2, "Два");
        map.put(3, "Три");
        java.util.List<MyData> myDataList = Arrays.asList(
                new MyData("first1", "second1", randomScore()),
                new MyData("first2", "second2", randomScore()),
                new MyData("first3", "second3", randomScore()));

        table1.setModel(new MyTableModel(myDataList));

//        class MyItem {
//            Integer id;
//            String text;
//
//            public MyItem(Integer id, String text) {
//                this.id = id;
//                this.text = text;
//            }
//
//            @Override
//            public String toString() {
//                return text;
//            }
//        }

//        ArrayList<MyItem> list = new ArrayList<>(map.entrySet()
//                .stream()
//                .map(e -> new MyItem(e.getKey(), e.getValue()))
//                .collect(Collectors.toList()));
//
////        comboBox1.setModel(new DefaultComboBoxModel(list.toArray()));
//
////        MyItem selectedItem = (MyItem) comboBox1.getModel().getSelectedItem();


    }

    private void onClick(ActionEvent e) {

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
