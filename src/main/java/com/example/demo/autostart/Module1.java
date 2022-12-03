package com.example.demo.autostart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Module1 extends JFrame {

    public Module1() throws HeadlessException {
        super("Module 1");
        setVisible(true);
        setSize(250, 150);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        JFrame frame = this;
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosed(e);
                int res = JOptionPane.showConfirmDialog(frame, "Close?");
                if (res == JOptionPane.YES_OPTION) {
                    dispose();
                }
            }
        });
    }
}
