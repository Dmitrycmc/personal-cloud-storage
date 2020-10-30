package ru.gb.client;

import java.nio.file.Paths;

public class Client {
    public static void main(String[] args) {
        Network network = new Network("localhost", 8189);
        network.start();

        network.send(Paths.get("client_storage/1.jpg"));
        network.send(Paths.get("client_storage/2.jpg"));
        network.send(Paths.get("client_storage/3.bmp"));
        network.send(Paths.get("client_storage/4.bmp"));
        network.waitForAnswer();
    }
}
