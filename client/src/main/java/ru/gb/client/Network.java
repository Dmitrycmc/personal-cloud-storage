package ru.gb.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import ru.gb.common.Constants;
import ru.gb.common.Status;
import ru.gb.common.messages.*;
import ru.gb.common.messages.Package;

import java.io.*;
import java.net.Socket;

class Network {
    private String domain;
    private int port;

    private ObjectDecoderInputStream in;
    private ObjectEncoderOutputStream out;

    Network(String domain, int port) {
        this.domain = domain;
        this.port = port;
    }

    private void sendObject(Object obj) throws IOException {
        out.writeObject(obj);
    }

    private Object waitForAnswer() {
        Object b = null;
        try {
            b = in.readObject();
        } catch (Exception e) {
            System.out.println("Соединение разорвано");
            System.exit(0);
        }
        return b;
    }

    public void postFiles(String path) throws IOException {
        sendObject(new PostFileRequest("client_storage/" + path));
        if (((Response) waitForAnswer()).getStatus() == Status.Failure) {
            System.out.println("Server error");
        }
        File f = new File("client_storage/" + path);
        long fileSize = f.length();
        long readBytesCounter = 0;
        FileInputStream fis = new FileInputStream(f);
        do {
            readBytesCounter += Constants.maxPackageSize;
            Package pkg = new Package(fis.readNBytes(Constants.maxPackageSize), readBytesCounter >= fileSize);

            sendObject(pkg);
            if (((Response) waitForAnswer()).getStatus() == Status.Failure) {
                System.out.println("Server error");
            } else {
                System.out.println("Package sent: " + pkg);
            }
        } while (readBytesCounter < fileSize);
        fis.close();
        System.out.println("File sent: " + path);
    }

    public void getFiles(String path) throws IOException {
        sendObject(new GetFileRequest(path));

        Response res = (Response) waitForAnswer();
        GetFileResponse response = (GetFileResponse) res;
        if (response.getStatus() == Status.Failure) {
            throw new IOException();
        }
        Package pkg;
        FileOutputStream fos = new FileOutputStream("client_storage/" + response.getFileName());
        do {
            pkg = (Package) waitForAnswer();
            fos.write(pkg.getData());
        } while (!pkg.isTerminate());
        fos.close();
        System.out.println("File received: " + response.getFileName());
    }

    public String[] getFilesList() throws Exception {
        return getFilesList("");
    }

    public String[] getFilesList(String path) throws Exception {
        sendObject(new GetFilesListRequest(path));
        GetFilesListResponse response = (GetFilesListResponse) waitForAnswer();
        if (response.getStatus() == Status.Failure) {
            System.out.println("Server error");
            throw new Exception();
        } else {
            System.out.println(String.join("\n", response.getFilesList()));
        }

        return response.getFilesList();
    }

    public void deleteFiles(String path) throws IOException {
        sendObject(new DeleteFileRequest(path));
        Response response = (Response) waitForAnswer();
        if (response.getStatus() == Status.Failure) {
            System.out.println("Server error");
        } else {
            System.out.println("Deleted file: " + path);
        }
    }

    public void start() {
        try {
            Socket socket = new Socket(domain, port);
            System.out.println("Connection established!");
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream(), Constants.maxObjectSize);
        } catch (IOException e) {
            System.out.println("Failed to connect!");
            System.exit(0);
        }
    }
}
