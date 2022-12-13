package com.example.demo.server;

import lombok.SneakyThrows;

import java.util.concurrent.ThreadLocalRandom;

public class ClientServerExampleMain {
    @SneakyThrows
    public static void main(String[] args) {
        //Эмуляция входящего аргумента, в реальности надо брать из args
        String startUrl = "shariktlt://randomString" + (ThreadLocalRandom.current().nextLong());

        ProtocolHandlerEventBus eventBus = ProtocolHandlerEventBus.getInstance();

        ClientServerExample clientServer = new ClientServerExample(startUrl, eventBus);
        clientServer.start();

        //Синхронизируемся с фоновым потоком
        if (!clientServer.exchange()) {
            //Это клиент, нормальная работа недопустима
            System.out.println("Is client, now exit");
            System.exit(0);
        }

        //Пройдя процедуру входа, когда дойдем до момента где создаются модули
        //Начинаем регистрировать наш обработчик, который получит стартовый урл если он был на старте
        eventBus.register((url) -> {
            System.out.println("Got url " + url);
        });


        //это симуляция работы приложения, что бы мейн не закрывался сам по себе. Только для примера
        clientServer.join();
    }
}
