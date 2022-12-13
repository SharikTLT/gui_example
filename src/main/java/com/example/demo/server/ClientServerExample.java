package com.example.demo.server;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

public class ClientServerExample extends Thread {

    public static final String PORT_FILE = "./localPort";
    public static final String LOCK_FILE = "./lockFile";
    public static final String LOCALHOST = "localhost";
    public static final int EXCHANGE_TIMEOUT_SEC = 1;
    public static final String NEWLINE = "\\n";

    private final ProtocolHandlerEventBus eventBus;
    private final Exchanger<Boolean> exchanger = new Exchanger<>();
    private final String startUrl;

    private ServerSocket serverSocket;

    public ClientServerExample(String startUrl, ProtocolHandlerEventBus eventBus) {
        this.startUrl = startUrl;
        this.eventBus = eventBus;
        eventBus.onUrlReceived(startUrl);
    }

    @SneakyThrows
    public boolean exchange() {
        return exchanger.exchange(null);
    }

    /**
     * Может быть 2 сценария
     * 1) Блокировка на файле удалась - мы первый экземпляр, то есть сервер.
     * 2) Блокировка на файле не удалась - мы не единственный экземпляр, то есть мы клиент.
     * <p>
     * Для сервера:
     * 1) Поднимаем серверный сокет;
     * 2) Пишем порт в файл;
     * 3) Синхронизируемся с мейном, мейн должен продолжить свою штатную работу;
     * 4) Уходим обрабатывать поступающие подключения по секету.
     * <p>
     * Для клиента:
     * 1) Читаем файл с портом;
     * 2) Пытаемся передать урл по секету на сервер;
     * 3) Если не удалось, возврат к п1;
     * 4) Синхронизируемся с мейном, мейн должен завершить свою работу через System.exit(0).
     */
    @Override
    public void run() {
        try (FileOutputStream fileOutputStream = new FileOutputStream(LOCK_FILE);
             FileChannel channel = fileOutputStream.getChannel();
             FileLock fileLock = channel.tryLock()) {
            if (fileLock == null) {
                sendToServerWithRetry();
                notifyMain(false);
                return;
            }

            serverSocket = new ServerSocket(0);
            writePortToFile(serverSocket.getLocalPort());
            notifyMain(true);

            runServer();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Запись порта поднятого сокета в PORT_FILE
     *
     * @param localPort
     */
    private void writePortToFile(int localPort) {
        byte[] bytes = String.valueOf(localPort).getBytes(StandardCharsets.UTF_8);
        try {
            Files.write(new File(PORT_FILE).toPath(), bytes, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Попытки отправить на сервер
     */
    private void sendToServerWithRetry() {
        int tryCount = 10;//на всякий случай ограничиваем число попыток
        while (tryCount-- > 0) {
            try (
                    FileInputStream fileInputStream = new FileInputStream(PORT_FILE);
                    Scanner scanner = new Scanner(fileInputStream)) {
                int serverPort = scanner.nextInt();

                if (sendToServer(serverPort)) {
                    return;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException("Не удалось отправить URL на экземпляр локального сервера");
    }

    /**
     * Попытка записи урл в открытый сокет
     *
     * @param serverPort
     * @return
     */
    private boolean sendToServer(int serverPort) {
        try (Socket socket = new Socket(LOCALHOST, serverPort)) {
            socket.getOutputStream().write((startUrl + NEWLINE).getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Запуск сервера в бесконечном цикле
     */
    private void runServer() {
        while (true) {
            try (
                    Socket client = serverSocket.accept();
                    Scanner scanner = new Scanner(client.getInputStream())) {
                String command = scanner.nextLine();
                if (command != null && !command.isEmpty()) {
                    sendToBusWithClearingTailNewline(command);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Передача урла в шину, с очисткой последнего символа переноса строки "\\n"
     *
     * @param command
     */
    private void sendToBusWithClearingTailNewline(String command) {
        if (command.endsWith(NEWLINE)) {
            command = command.substring(0, command.length() - NEWLINE.length());
        }
        eventBus.onUrlReceived(command);
    }

    /**
     * Синхронизация с мейном через exchanger
     * @param res
     */
    private void notifyMain(boolean res) {
        try {
            exchanger.exchange(res, EXCHANGE_TIMEOUT_SEC, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
