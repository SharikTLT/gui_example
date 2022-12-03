package com.example.demo;

import lombok.SneakyThrows;

import java.net.URL;

public class ProtocolUrlHolder {
    private URL url;
    private boolean used;

    @SneakyThrows
    public synchronized void save(String rawUrl) {
        url = new URL(rawUrl.replaceFirst("shariktlt://", "http://unusedhost/"));
        used = false;
    }

    public synchronized URL getUnused() {
        if (url != null) {
            URL res = url;
            url = null;
            return res;
        }
        return null;
    }

    public synchronized URL getUrl() {
        return url;
    }

    private static ProtocolUrlHolder INSTANCE = new ProtocolUrlHolder();

    public static ProtocolUrlHolder getInstance() {
        return INSTANCE;
    }
}
