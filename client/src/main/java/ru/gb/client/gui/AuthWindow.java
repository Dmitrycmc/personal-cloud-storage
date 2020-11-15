package ru.gb.client.gui;

import ru.gb.client.Network;
import ru.gb.client.exceptions.UnauthorizedException;

import javax.swing.*;
import java.awt.*;

class AuthWindow extends JFrame {
    private Network network;
    private JTextField loginField;
    private JTextField passwordField;

    private void login(boolean newAccount) {
        String login = loginField.getText();
        String password = passwordField.getText();
        if (login.length() > 0 && password.length() > 0) {
            try {
                if (newAccount) {
                    network.createUser(login, password);
                } else {
                    network.login(login, password);
                }
                loginField.setText("");
                passwordField.setText("");
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
        setBounds(300, 300, 400, 200);
        setResizable(false);

        setLayout(new GridLayout(4, 1, 5, 5));

        add(new JLabel("Enter login and password:"));

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(1, 2));
        loginPanel.add(new JLabel("Login"));
        loginField = new JTextField();
        loginField.addActionListener(e -> login(false));
        loginPanel.add(loginField);
        add(loginPanel);

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new GridLayout(1, 2));
        passwordPanel.add(new JLabel("Password"));
        passwordField = new JTextField();
        passwordField.addActionListener(e -> login(false));
        passwordPanel.add(passwordField);
        add(passwordPanel);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 5, 5));
        JButton loginButton = new JButton("Sign in");
        loginButton.addActionListener(e -> login(false));
        buttonsPanel.add(loginButton);
        JButton singUpButton = new JButton("Create account");
        singUpButton.addActionListener(e -> login(true));
        buttonsPanel.add(singUpButton);
        add(buttonsPanel);

        setVisible(true);
    }
}
