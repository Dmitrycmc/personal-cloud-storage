package ru.gb.client;

import java.io.IOException;
import java.nio.file.Paths;

public class Client {
    public static void main(String[] args) {
        Network network = new Network("localhost", 8189);
        network.start();

        new Thread(() -> {
            while (true) {
                network.waitForAnswer(); // ставлю обработчик на прием байтов
            }
        }).start();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        network.send(Paths.get("client_storage/1.jpg"));
//        network.send(Paths.get("client_storage/2.jpg"));
//        network.send(Paths.get("client_storage/3.bmp"));
//        network.send(Paths.get("client_storage/4.bmp"));
        try {
            network.out.write(new byte[] {1, 2, 3}); // байты отправляется
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
