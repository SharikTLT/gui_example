package com.example.demo;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyHeaderModel extends DefaultTableColumnModel {

    public MyHeaderModel(String... headers) {

        List<TableColumn> collect = Arrays.stream(headers).map((s) -> {
            TableColumn column = new TableColumn();
            column.setHeaderValue(s);
            column.setModelIndex(this.getColumnCount());
            return column;
        }).collect(Collectors.toList());

        for (int i = 0; i < collect.size(); i++) {
            TableColumn col = collect.get(i);
            col.setModelIndex(i);
            this.addColumn(col);
        }
    }

}
