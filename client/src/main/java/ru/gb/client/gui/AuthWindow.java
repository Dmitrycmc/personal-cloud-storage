package ru.gb.client.gui;

import ru.gb.client.Network;
import ru.gb.client.exceptions.UnauthorizedException;

import javax.swing.*;
import java.awt.*;

class AuthWindow extends JFrame {
    private Network network;
    private JTextField loginField;
    private JTextField passwordField;

    private void submit() {
        String login = loginField.getText();
        String password = passwordField.getText();
        if (login.length() > 0 && password.length() > 0) {
            try {
                network.login(login, password);
                MainWindow mainWindow = new MainWindow(network, this);
                mainWindow.setTitle("Personal cloud storage [" + login + "]");
                setVisible(false);
            } catch (UnauthorizedException e) {
                JOptionPane.showMessageDialog(null, "Invalid login or password", "Unauthorized", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    AuthWindow(Network network) {
        this.network = network;

        setTitle("Sign in");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(300, 300, 400, 120);
        setResizable(false);

        setLayout(new GridLayout(3, 1));

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(1, 2));
        loginPanel.add(new JLabel("Login"));
        loginField = new JTextField();
        loginField.addActionListener(e -> submit());
        loginPanel.add(loginField);
        add(loginPanel);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new GridLayout(1, 2));
        passwordPanel.add(new JLabel("Password"));
        passwordField = new JTextField();
        passwordField.addActionListener(e -> submit());
        passwordPanel.add(passwordField);
        add(passwordPanel);

        JButton sendButton = new JButton("Sign in");
        sendButton.addActionListener(e -> submit());
        add(sendButton);

        setVisible(true);
    }
}
