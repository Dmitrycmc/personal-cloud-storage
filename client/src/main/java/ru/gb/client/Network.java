package ru.gb.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.gb.client.exceptions.ServerException;
import ru.gb.client.exceptions.UnauthorizedException;
import ru.gb.common.Constants;
import ru.gb.common.Status;
import ru.gb.common.messages.*;
import ru.gb.common.messages.Package;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;

public class Network {
    private String domain;
    private int port;
    private String token;
    private final Logger logger = LogManager.getLogger(Network.class);

    private ObjectDecoderInputStream in;
    private ObjectEncoderOutputStream out;

    public Network(String domain, int port) {
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

    private void checkErrors(Response response) throws Exception {
        if (response.getStatus() == Status.Failure) {
            logger.error("Server error");
            throw new ServerException();
        }
        if (response.getStatus() == Status.Unauthorized) {
            logger.error("Unauthorized");
            throw new UnauthorizedException();
        }
    }

    public void postFile(String path) throws Exception {
        PostFileRequest request = new PostFileRequest(path);
        request.setToken(token);
        sendObject(request);
        Response response = (Response) waitForAnswer();

        checkErrors(response);

        File f = new File(path);
        long fileSize = f.length();
        long readBytesCounter = 0;
        FileInputStream fis = new FileInputStream(f);
        do {
            readBytesCounter += Constants.maxPackageSize;
            Package pkg = new Package(fis.readNBytes(Constants.maxPackageSize), readBytesCounter >= fileSize);

            sendObject(pkg);
            response = (Response) waitForAnswer();

            checkErrors(response);
        } while (readBytesCounter < fileSize);
        fis.close();
        logger.info("File sent: " + path);
    }

    public void getFile(String serverPath) throws Exception {
        String clientPath = Paths.get(serverPath).getFileName().toString();
        getFile(serverPath, "client_storage/" + clientPath);
    }

    public void getFile(String serverPath, String clientPath) throws Exception {
        GetFileRequest request = new GetFileRequest(serverPath);
        request.setToken(token);
        sendObject(request);

        Response response = (Response) waitForAnswer();

        checkErrors(response);

        Package pkg;
        FileOutputStream fos = new FileOutputStream(clientPath);
        do {
            pkg = (Package) waitForAnswer();
            fos.write(pkg.getData());
        } while (!pkg.isTerminate());
        fos.close();
        logger.info("File received: " + ((GetFileResponse)response).getFileName());
    }

    public String[] getList() throws Exception {
        return getList("");
    }

    public String[] getList(String path) throws Exception {
        GetFilesListRequest request = new GetFilesListRequest(path);
        request.setToken(token);
        sendObject(request);
        Response response = (Response) waitForAnswer();
        checkErrors(response);

        return ((GetFilesListResponse) response).getFilesList();
    }

    public void deleteFile(String path) throws Exception {
        DeleteFileRequest request = new DeleteFileRequest(path);
        request.setToken(token);
        sendObject(request);
        Response response = (Response) waitForAnswer();

        checkErrors(response);

        logger.info("Deleted file: " + path);
    }

    public void patchFile(String oldPath, String newPath) throws Exception {
        PatchFileRequest request = new PatchFileRequest(oldPath, newPath);
        request.setToken(token);
        sendObject(request);
        Response response = (Response) waitForAnswer();

        checkErrors(response);

        logger.info("Renamed file: from" + oldPath + " to " + newPath);
    }

    public void login(String login, String password) throws Exception {
        LoginRequest request = new LoginRequest(login, password);
        sendObject(request);
        Response response = (Response) waitForAnswer();
        checkErrors(response);
        logger.info("You logged in as " + login);
        token = ((LoginResponse)response).getToken();
    }

    public void logout() throws Exception {
        LogoutRequest request = new LogoutRequest();
        request.setToken(token);
        sendObject(request);
        Response response = (Response) waitForAnswer();
        checkErrors(response);
        logger.info("You logged out!");
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
