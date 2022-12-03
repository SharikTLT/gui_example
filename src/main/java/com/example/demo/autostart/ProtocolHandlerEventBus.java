package com.example.demo.autostart;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class ProtocolHandlerEventBus {

    private List<Consumer<URL>> consumers = new CopyOnWriteArrayList<>();

    public void register(Consumer<URL> consumer) {
        consumers.add(consumer);
    }

    public void onUrlReceived(URL url) {
        consumers.forEach((consumer -> {
            try {
                consumer.accept(url);
            } catch (Exception e) {
                //log error here
            }
        }));
    }

    private static ProtocolHandlerEventBus INSTANCE = new ProtocolHandlerEventBus();

    public static ProtocolHandlerEventBus getInstance() {
        return INSTANCE;
    }
}
