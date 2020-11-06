package ru.gb.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.gb.common.Constants;
import ru.gb.common.Status;
import ru.gb.common.messages.*;
import ru.gb.common.messages.Package;

import java.io.*;
import java.net.Socket;

class Network {
    private String domain;
    private int port;
    private final Logger logger = LogManager.getLogger(Network.class);

    private ObjectDecoderInputStream in;
    private ObjectEncoderOutputStream out;

    Network(String domain, int port) {
        this.domain = domain;
        this.port = port;
    }

    private void sendObject(Object obj) throws IOException {
        logger.trace("Sent to server: " + obj);
        out.writeObject(obj);
    }

    private Object waitForAnswer() {
        Object obj = null;
        try {
            obj = in.readObject();
            logger.trace("Received from server: " + obj);
        } catch (Exception e) {
            logger.fatal("Connection lost");
            System.exit(0);
        }
        return obj;
    }

    public void postFiles(String path) throws Exception {
        sendObject(new PostFileRequest("client_storage/" + path));
        if (((Response) waitForAnswer()).getStatus() == Status.Failure) {
            logger.error("Server error");
            throw new Exception();
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
                logger.error("Server error");
                throw new Exception();
            }
        } while (readBytesCounter < fileSize);
        fis.close();
        logger.info("File sent: " + path);
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
        logger.info("File received: " + response.getFileName());
    }

    public String[] getFilesList() throws Exception {
        return getFilesList("");
    }

    public String[] getFilesList(String path) throws Exception {
        sendObject(new GetFilesListRequest(path));
        GetFilesListResponse response = (GetFilesListResponse) waitForAnswer();
        if (response.getStatus() == Status.Failure) {
            logger.error("Server error");
            throw new Exception();
        }

        return response.getFilesList();
    }

    public void deleteFiles(String path) throws IOException {
        sendObject(new DeleteFileRequest(path));
        Response response = (Response) waitForAnswer();
        if (response.getStatus() == Status.Failure) {
            logger.error("Server error");
        } else {
            logger.info("Deleted file: " + path);
        }
    }

    public void patchFiles(String oldPath, String newPath) throws IOException {
        sendObject(new PatchFileRequest(oldPath, newPath));
        Response response = (Response) waitForAnswer();
        if (response.getStatus() == Status.Failure) {
            logger.error("Server error");
        } else {
            logger.info("Renamed file: from" + oldPath + " to " + newPath);
        }
    }

    public void login(String login, String password) throws IOException {
        sendObject(new LoginRequest(login, password));
        Response response = (Response) waitForAnswer();
        if (response.getStatus() == Status.Failure) {
            logger.error("Server error");
        } else {
            logger.info("You logged in as " + login);
        }
    }

    public void start() {
        try {
            Socket socket = new Socket(domain, port);
            logger.info("Connection established!");
            out = new ObjectEncoderOutputStream(socket.getOutputStream());
            in = new ObjectDecoderInputStream(socket.getInputStream(), Constants.maxObjectSize);
        } catch (IOException e) {
            logger.fatal("Failed to connect!");
            System.exit(0);
        }
    }
}
