package ru.gb.client.gui;

import ru.gb.client.Network;
import ru.gb.common.Constants;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private Network network;

    public MainWindow() throws HeadlessException {
        network = new Network("localhost", Constants.port);
        network.start();

        setBounds(300, 300, 800, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        new AuthWindow(network, this);
    }

    public static void main(String[] args) {
        new MainWindow();
    }
}
