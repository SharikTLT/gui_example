package com.example.demo;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        if(args.length > 0){
            try {
                ProtocolUrlHolder.getInstance().save(args[0]);
            }catch (Exception e){
                //ignore
            }
        }
        MyForm frame = new MyForm("My First GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300,300);
        frame.setVisible(true);
        frame.setContentPane(frame.getMyPanel());
    }
}
