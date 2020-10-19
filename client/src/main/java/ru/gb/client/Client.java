package ru.gb.client;

import java.io.IOException;
import java.nio.file.Paths;

public class Client {
    public static void main(String[] args) throws IOException {
        Network network = new Network("localhost", 8189);
        network.start();

//        network.send(Paths.get("client_storage/2.txt"));
//        network.send(Paths.get("client_storage/1.txt"));
//        network.send(Paths.get("client_storage/1.bmp"));
        network.send(Paths.get("client_storage/1.jpg"));
        network.waitForAnswer();
    }
}
