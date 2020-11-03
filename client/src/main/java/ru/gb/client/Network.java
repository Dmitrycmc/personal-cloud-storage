package ru.gb.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Path;

class Network {
    private String domain;
    private int port;

    private ObjectDecoderInputStream in;
    private ObjectEncoderOutputStream out;

    Network(String domain, int port) {
        this.domain = domain;
        this.port = port;
    }

    private void sendBytesArray(byte[] arr) throws IOException {
        for (byte b : arr) {
            out.writeByte(b);
        }
    }

    public void sendObject(Object obj) throws IOException {
        out.writeObject(obj);
    }

    private void send(long n) throws IOException {
        ByteBuffer b = ByteBuffer.allocate(8);
        b.putLong(n);
        sendBytesArray(b.array());
    }

    void send(byte b) throws IOException {
        out.writeByte(b);
    }

    void send(String s) throws IOException {
        send((byte)s.length());
        sendBytesArray(s.getBytes());
    }

    private void send(FileInputStream fis) throws IOException {
        int b;
        while (true){
            b = fis.read();
            if (b == -1) break;
            out.writeByte(b);
        }
    }

    void send(Path path) {
        String filename = path.getFileName().toString();
        try {
            FileInputStream fis = new FileInputStream(path.toString());
            send(filename);
            send(fis.getChannel().size());
            send(fis);
            fis.close();
            System.out.println("Успешно передан файл " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка передачи файла " + filename);
        }
    }

    Object waitForAnswer() {
        Object b = 0;
        try {
            b = in.readObject();
        } catch (Exception e) {
            System.out.println("Соединение разорвано");
            System.exit(0);
        }
        return b;
    }

    void start() {
        try {
            Socket socket = new Socket(domain, port);
            System.out.println("Соединение установлено!");
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream(), 50 * 1024 * 1024);
        } catch (IOException e) {
            System.out.println("Не удалось подключиться!");
            System.exit(0);
        }
    }
}
