package ru.gb.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.gb.common.Constants;
import ru.gb.common.Status;
import ru.gb.common.messages.*;
import ru.gb.common.messages.Package;

import java.io.*;

public class Handler extends ChannelInboundHandlerAdapter {
    FileOutputStream fos;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object request) {
        System.out.println("\n" + request);
        Response response;

        if (request instanceof GetFileRequest) {
            FileInputStream fis = null;
            String path = "server_storage/" + ((GetFileRequest) request).getPath();
            File f = new File(path);
            long fileSize = f.length();
            long readBytesCounter = 0;
            try {
                fis = new FileInputStream(f);
                ctx.writeAndFlush(new GetFileResponse(path));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                while (true) {
                    readBytesCounter += Constants.maxPackageSize;
                    if (readBytesCounter >= fileSize) {
                        Package pkg = new Package(fis.readNBytes(Constants.maxPackageSize), true);
                        ctx.writeAndFlush(pkg);
                        System.out.println("Package sent: " + pkg);
                        break;
                    } else {
                        Package pkg = new Package(fis.readNBytes(Constants.maxPackageSize), false);
                        ctx.writeAndFlush(pkg);
                        System.out.println("Package Sent: " + pkg);
                    }
                }
                System.out.println("File sent: " + path);
            } catch (Exception e) {
                System.out.println("Sending error");
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
            ctx.writeAndFlush(response);
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
            ctx.writeAndFlush(response);
        }
        if (request instanceof GetFilesListRequest) {
            response = new GetFilesListResponse("server_storage/" + ((GetFilesListRequest) request).getPath());
            ctx.writeAndFlush(response);
        }
        if (request instanceof DeleteFileRequest) {
            response = new DeleteFileResponse("server_storage/" + ((DeleteFileRequest) request).getPath());
            ctx.writeAndFlush(response);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
