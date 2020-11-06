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

public class Handler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LogManager.getLogger(Handler.class);
    FileOutputStream fos;

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

        if (request instanceof GetFileRequest) {
            FileInputStream fis = null;
            String path = "server_storage/" + ((GetFileRequest) request).getPath();
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
                while (true) {
                    readBytesCounter += Constants.maxPackageSize;
                    if (readBytesCounter >= fileSize) {
                        Package pkg = new Package(fis.readNBytes(Constants.maxPackageSize), true);
                        send(pkg);
                        break;
                    } else {
                        Package pkg = new Package(fis.readNBytes(Constants.maxPackageSize), false);
                        send(pkg);
                    }
                }
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
        }
        if (request instanceof PostFileRequest) {
            try {
                fos = new FileOutputStream("server_storage/" + ((PostFileRequest) request).getFileName());
                response = new Response(Status.Success);
            } catch (IOException e) {
                response = new Response(Status.Failure);
            }
            send(response);
        }
        if (request instanceof Package) {
            try {
                fos.write(((Package) request).getData());
                if (((Package) request).isTerminate()) {
                    fos.close();
                }
                response = new Response(Status.Success);
            } catch (IOException e) {
                response = new Response(Status.Failure);
            }
            send(response);
        }
        if (request instanceof GetFilesListRequest) {
            response = new GetFilesListResponse("server_storage/" + ((GetFilesListRequest) request).getPath());
            send(response);
        }
        if (request instanceof DeleteFileRequest) {
            response = new DeleteFileResponse("server_storage/" + ((DeleteFileRequest) request).getPath());
            send(response);
        }
        if (request instanceof PatchFileRequest) {
            response = new PatchFileResponse("server_storage/" + ((PatchFileRequest) request).getOldPath(), "server_storage/" + ((PatchFileRequest) request).getNewPath());
            send(response);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.info("Connection is closed");
        ctx.close();
    }
}
