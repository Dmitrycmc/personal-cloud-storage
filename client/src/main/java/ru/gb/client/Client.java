package ru.gb.client;

public class Client {
    public static void main(String[] args) {
        Network network = new Network("localhost", 8189);
        network.start();
        network.send("abc".getBytes());
        network.waitForAnswer();
    }
}
