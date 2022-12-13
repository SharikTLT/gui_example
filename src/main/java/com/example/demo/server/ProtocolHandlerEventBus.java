package com.example.demo.server;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ProtocolHandlerEventBus {
    public static final String HTTP_UNUSEDHOST = "http://unusedhost/";

    /**
     * Нужно заменить на свой протокол
     */
    public static final String HANDLER_PREFIX = "shariktlt://";

    private static ProtocolHandlerEventBus INSTANCE = new ProtocolHandlerEventBus();

    public static ProtocolHandlerEventBus getInstance() {
        return INSTANCE;
    }

    private List<Consumer<URL>> consumers = new CopyOnWriteArrayList<>();

    private AtomicReference<URL> lastUrl = new AtomicReference<>();

    /**
     * Регистрирует и выполняет последний урл
     *
     * @param consumer
     */
    public void register(Consumer<URL> consumer) {
        consumers.add(consumer);
        if (lastUrl.get() != null) {
            safeAccept(lastUrl.get(), consumer);
        }
    }

    /**
     * Перегрузка методов, для работы с чистой строкой вместо объекта URL
     *
     * @param rawUrl
     */
    public void onUrlReceived(String rawUrl) {
        if (rawUrl == null) {
            return;
        }
        try {
            onUrlReceived(new URL(rawUrl.replaceFirst(HANDLER_PREFIX, HTTP_UNUSEDHOST)));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Сохраняем последний полученный урл и передаем его зарегистрированным консьюмерам
     *
     * @param url
     */
    public void onUrlReceived(URL url) {
        lastUrl.set(url);
        consumers.forEach((consumer -> {
            safeAccept(url, consumer);
        }));
    }

    /**
     * Безопасная попытка принять консьюмером урл, все исключения игнорируются
     *
     * @param url
     * @param consumer
     */
    private void safeAccept(URL url, Consumer<URL> consumer) {
        try {
            consumer.accept(url);
        } catch (Exception e) {
            //log error here
        }
    }
}
