package ru.gb.client.gui;

import ru.gb.client.Network;
import ru.gb.common.Constants;

public class MainClass {
    public static void main(String[] args) {
        Network network = new Network("localhost", Constants.port);
        network.start();

        new AuthWindow(network);
    }
}
