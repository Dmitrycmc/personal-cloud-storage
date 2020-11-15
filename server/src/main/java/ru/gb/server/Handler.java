package ru.gb.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.gb.common.Constants;
import ru.gb.common.Status;
import ru.gb.common.messages.*;
import ru.gb.common.messages.Package;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Handler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LogManager.getLogger(Handler.class);
    private FileOutputStream fos;

    private String login = null;
    private String token = null;

    private String getStorageBasePath() {
        return "server_storage/" + login + "/";
    }

    private ChannelHandlerContext ctx;

    private void send(Object obj) {
        logger.trace("Sent to client: " + obj);
        ctx.writeAndFlush(obj);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object request) {
        this.ctx = ctx;
        logger.trace("Received from client: " + request);
        Response response;

        if (request instanceof CreateUserRequest) {
            String login = ((CreateUserRequest) request).getLogin();
            String password = ((CreateUserRequest) request).getPassword();
            if (createUser(login, password)) {
                response = new LoginResponse(token);
            } else {
                logger.error("Error");
                response = new Response(Status.Failure);
            }
            send(response);
            return;
        }
        if (request instanceof LoginRequest) {
            String login = ((LoginRequest) request).getLogin();
            String password = ((LoginRequest) request).getPassword();
            if (login(login, password)) {
                logger.info("Logged in as " + login);
                response = new LoginResponse(token);
            } else {
                logger.error("Invalid authorized data");
                response = new Response(Status.Unauthorized);
            }
            send(response);
            return;
        }
        if (((Request) request).getToken() == null) {
            logger.error("Unauthorized");
            send(new Response(Status.Unauthorized));
            return;
        }
        if (!((Request) request).getToken().equals(token)) {
            logger.error("Invalid session token");
            send(new Response(Status.Unauthorized));
            return;
        }
        if (request instanceof GetFileRequest) {
            FileInputStream fis;
            String path = getStorageBasePath() + ((GetFileRequest) request).getPath();
            File f = new File(path);
            long fileSize = f.length();
            long readBytesCounter = 0;
            try {
                fis = new FileInputStream(f);
                send(new GetFileResponse(path));
            } catch (FileNotFoundException e) {
                send(new Response(false));
                logger.error("File not found: " + path);
                return;
            }
            try {
                do {
                    readBytesCounter += Constants.packageSize;
                    Package pkg = new Package(fis.readNBytes(Constants.packageSize), readBytesCounter >= fileSize);
                    send(pkg);
                } while (readBytesCounter < fileSize);
                logger.info("File sent: " + path);
            } catch (Exception e) {
                logger.error("Sending error");
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            return;
        }
        if (request instanceof PostFileRequest) {
            String fileName = ((PostFileRequest) request).getFileName();
            try {
                fos = new FileOutputStream(getStorageBasePath() + fileName);
                response = new Response(Status.Success);
            } catch (IOException e) {
                response = new Response(Status.Failure);
            }
            send(response);
            return;
        }
        if (request instanceof Package) {
            try {
                byte[] data = ((Package) request).getData();
                boolean isTerminate = ((Package) request).isTerminate();
                fos.write(data);
                if (isTerminate) {
                    fos.close();
                }
                response = new Response(Status.Success);
            } catch (IOException e) {
                response = new Response(Status.Failure);
            }
            send(response);
            return;
        }
        if (request instanceof GetFilesListRequest) {
            String path = ((GetFilesListRequest) request).getPath();
            String[] filesList = getFilesList(getStorageBasePath() + path);
            response = new GetFilesListResponse(filesList);
            send(response);
            return;
        }
        if (request instanceof DeleteFileRequest) {
            String path = ((DeleteFileRequest) request).getPath();
            boolean resultFlag = deleteFile(getStorageBasePath() + path);
            response = new Response(resultFlag);
            send(response);
            return;
        }
        if (request instanceof PatchFileRequest) {
            String oldPath = ((PatchFileRequest) request).getOldPath();
            String newPath = ((PatchFileRequest) request).getNewPath();
            boolean resultFlag = patchFile(getStorageBasePath() + oldPath, getStorageBasePath() + newPath);
            response = new Response(resultFlag);
            send(response);
            return;
        }
        if (request instanceof LogoutRequest) {
            logout();
            send(new Response(true));
        }
    }

    private boolean patchFile(String oldPath, String newPath) {
        try {
            File file1 = new File(oldPath);
            File file2 = new File(newPath);
            if (!file1.exists() || file2.exists() || !file1.renameTo(file2)) {
                throw new Exception();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean deleteFile(String path) {
        try {
            Files.delete(Paths.get(path));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String[] getFilesList(String path) {
        try {
            File folder = new File(path);
            return Arrays.stream(folder.listFiles()).sorted((f1, f2) -> {
                boolean dir1 = f1.isDirectory();
                boolean dir2 = f2.isDirectory();
                if (dir1 == dir2) {
                    return f1.getName().compareTo(f2.getName());
                }
                return dir1 ? -1 : 1;
            }).map(file -> file.getName() + (file.isDirectory() ? "/" : "")).toArray(String[]::new);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String generateRandomString(int n) {
        String AlphaNumericString = "abcdefghijklmnopqrstuvxyz" + "0123456789";

        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {

            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    private String generateToken() {
        String[] strings = new String[4];
        for (int i = 0; i < 4; i++) {
            strings[i] = generateRandomString(4);
        }
        return Arrays.stream(strings).collect(Collectors.joining("-"));
    }

    private boolean createUser(String login, String password) {
        try (JdbcClass db = new JdbcClass(logger)) {
            Path path = Paths.get("server_storage/" + login);
            Files.createDirectories(path);
            logger.info("Directory " + "server_storage/" + login + " is created!");

            db.insertUser(login, password);
            logger.info("User " + login + " is created!");
            this.token = generateToken();
            this.login = login;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean login(String login, String password) {
        try (JdbcClass db = new JdbcClass(logger)) {
            if (db.authUser(login, password)) {
                this.token = generateToken();
                this.login = login;
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void logout() {
        login = null;
        token = null;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info("Connection is closed");
        ctx.close();
    }
}
