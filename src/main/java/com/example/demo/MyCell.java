package com.example.demo;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MyCell extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
    Map<String, JButton> map = new HashMap<>();



    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String key = row + "_" + column;
        return getButt(key, value);
    }

    private JButton getButt(String key, Object cellValueArg) {
        if (map.containsKey(key)) {
            return map.get(key);
        }

        JButton action = new JButton("Action");

        action.addActionListener((a) -> {

            System.out.println("Action triggered" + cellValueArg);
        });
        map.put(key, action);
        return action;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        String key = row + "_" + column;
        return getButt(key, value);
    }


    @Override
    public Object getCellEditorValue() {
        return null;
    }

}
