package ru.gb.client;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Path;

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
            for (byte b : arr) {
                out.writeByte(b);
            }
        } catch (IOException e) {
            System.out.println("Соединение разорвано");
            System.exit(0);
        }
    }

    private void send(FileInputStream fis) throws IOException {
        int batch = (int)Math.ceil((float)fis.getChannel().size() / 100);
        int i = 0;
        int progress = 0;
        int b;
        while (true){
            b = fis.read();
            if (b == -1) break;
            if (batch > 0 && i % batch == 0) {
                System.out.println(progress++ + "%");
            }
            out.writeByte(b);
            i++;
        }
    }

    private void send(long n) {
        ByteBuffer b = ByteBuffer.allocate(8);
        b.putLong(n);
        send(b.array());
    }

    private void send(String s) {
        send(s.length());
        send(s.getBytes());
    }

    void send(Path path) throws IOException {
        String filename = path.getFileName().toString();
        FileInputStream fis = new FileInputStream(path.toString());
        send(filename);
        send(fis.getChannel().size());
        send(fis);
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
