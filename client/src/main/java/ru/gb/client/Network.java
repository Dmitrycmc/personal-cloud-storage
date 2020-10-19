package ru.gb.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Files;

class Network {
    private String domain;
    private int port;

    private DataInputStream in;
    private DataOutputStream out;

    Network(String domain, int port) {
        this.domain = domain;
        this.port = port;
    }

    private void send(byte[] arr) {
        try {
            for (byte b: arr) {
                out.writeByte(b);
                System.out.println("Отпарвлен байт: " + b);
            }
        } catch (IOException e) {
            System.out.println("Соединение разорвано");
            System.exit(0);
        }
    }

    private void send(int n) {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(n);
        send(b.array());
    }

    private void send(String s) {
        send(s.length());
        send(s.getBytes());
    }

    void send(Path path) throws IOException {
        String filename = path.getFileName().toString();
        byte[] data = Files.readAllBytes(path);
        send(filename);
        send(data.length);
        send(data);
    }

    String waitForAnswer() {
        String message = "";
        try {
            message = in.readUTF();
            System.out.println("Получено сообщение: " + message);
        } catch (Exception e) {
            System.out.println("Соединение разорвано");
            System.exit(0);
        }
        return message;
    }

    void start() {
        try {
            Socket socket = new Socket(domain, port);
            System.out.println("Соединение установлено!");
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            System.out.println("Не удалось подключиться!");
            System.exit(0);
        }
    }
}
