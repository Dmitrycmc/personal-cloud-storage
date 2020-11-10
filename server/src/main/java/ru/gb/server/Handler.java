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
import java.nio.file.Paths;
import java.util.Arrays;

public class Handler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LogManager.getLogger(Handler.class);
    private FileOutputStream fos;

    private String login = null;

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

        if (request instanceof LoginRequest) {
            String login = ((LoginRequest) request).getLogin();
            String password = ((LoginRequest) request).getPassword();
            if (isSignInDataCorrect(login, password)) {
                this.login = login;
                logger.info("Logged in as " + login);
                response = new Response(Status.Success);
            } else {
                logger.error("Invalid authorized data");
                response = new Response(Status.Failure);
            }
            send(response);
            return;
        }
        if (login == null) {
            logger.error("Unauthorized");
            send(new Response(Status.Unauthorized));
            return;
        }
        if (request instanceof GetFileRequest) {
            FileInputStream fis = null;
            String path = getStorageBasePath() + ((GetFileRequest) request).getPath();
            File f = new File(path);
            long fileSize = f.length();
            long readBytesCounter = 0;
            try {
                fis = new FileInputStream(f);
                send(new GetFileResponse(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                do {
                    readBytesCounter += Constants.maxPackageSize;
                    Package pkg = new Package(fis.readNBytes(Constants.maxPackageSize), readBytesCounter >= fileSize);
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
            boolean resultFlag = patchFile(oldPath, newPath);
            response = new Response(resultFlag);
            send(response);
            return;
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

    private boolean isSignInDataCorrect(String login, String password) {
        // todo: store users in DB
        // todo: logout
        // todo: creating users
        return login.equals("Dima") && password.equals("0000");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info("Connection is closed");
        ctx.close();
    }
}
