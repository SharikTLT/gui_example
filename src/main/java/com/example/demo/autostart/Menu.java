package com.example.demo.autostart;

import com.example.demo.ProtocolUrlHolder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.awt.event.WindowEvent.WINDOW_CLOSING;

public class Menu extends JFrame {

    private Map<Class<?>, AtomicBoolean> openModuleRegistry = new ConcurrentHashMap<>();


    public Menu() throws HeadlessException {
        super("menu");
        setLayout(new FlowLayout());
        setSize(100, 100);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JButton btn1 = new JButton("Module 1");
        add(btn1);
        btn1.addActionListener((a) -> {
            loadModule(1);
        });

        JButton btn2 = new JButton("Module 2");
        add(btn2);
        btn2.addActionListener((a) -> {
            loadModule(2);
        });

        setVisible(true);

        ProtocolHandlerEventBus.getInstance().register((url -> {
            if (url.getPath().contains("module")) {
                String[] args = url.getPath().split("\\/");
                loadModule(Integer.parseInt(args[2]));
            }
        }));
        if (ProtocolUrlHolder.getInstance().hasUrl()) {
            URL url = ProtocolUrlHolder.getInstance().getUnused();
            ProtocolHandlerEventBus.getInstance().onUrlReceived(url);
        }

        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String line = null;
            ProtocolUrlHolder urlHolder = ProtocolUrlHolder.getInstance();
            ProtocolHandlerEventBus bus = ProtocolHandlerEventBus.getInstance();
            while (!"exit".equals(line = scanner.nextLine())) {
                try {
                    urlHolder.save(line);
                    bus.onUrlReceived(urlHolder.getUnused());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void loadModule(int i) {
        if (i == 1) {
            openSingle(Module1.class);
        } else {
            openSingle(Module2.class);
        }
    }


    private void openSingle(Class<? extends JFrame> className) {
        if (!openModuleRegistry.containsKey(className)) {
            openModuleRegistry.putIfAbsent(className, new AtomicBoolean());
        }
        if (!openModuleRegistry.get(className).get()) {
            if (openModuleRegistry.get(className).compareAndSet(false, true)) {
                try {
                    JFrame jFrame = className.getDeclaredConstructor().newInstance();
                    jFrame.addWindowListener(new WindowAdapter() {
                        @Override
                        public void windowClosed(WindowEvent e) {
                            super.windowClosed(e);
                            openModuleRegistry.get(className).set(false);
                            System.out.println("open lock");
                        }
                    });
                } catch (Exception e) {
                    //ignore for test only
                }
            }
        }
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            ProtocolUrlHolder.getInstance().save(args[0]);
        }
        new Menu();
    }
}
