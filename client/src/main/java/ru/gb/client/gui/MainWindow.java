package ru.gb.client.gui;

import ru.gb.client.Network;

import javax.swing.*;
import java.awt.*;

class MainWindow extends JFrame {
    MainWindow(Network network) throws HeadlessException {
        setBounds(300, 300, 800, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setVisible(true);
    }
}
