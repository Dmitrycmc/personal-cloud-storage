package ru.gb.client;

import ru.gb.common.Constants;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    private Network network;

    public GUI() throws HeadlessException {
        network = new Network("localhost", Constants.port);
        network.start();

        setBounds(300, 300, 800, 400);
        setTitle("Personal cloud storage");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setVisible(true);
    }

    public static void main(String[] args) {
        new GUI();
    }
}
