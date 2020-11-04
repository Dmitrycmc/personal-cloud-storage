package ru.gb.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.gb.common.Status;
import ru.gb.common.messages.*;
import ru.gb.common.messages.Package;

import java.io.FileOutputStream;
import java.io.IOException;

public class Handler extends ChannelInboundHandlerAdapter {
    FileOutputStream fos;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object request) {
        System.out.println(request);
        Response response = null;

        if (request instanceof GetFileRequest) {
            response = new GetFileResponse("server_storage/" + ((GetFileRequest) request).getPath());
        }
        if (request instanceof PostFileRequest) {
            try {
                fos = new FileOutputStream("server_storage/" + ((PostFileRequest) request).getFileName(), true);
                response = new Response(Status.Success);
            } catch (IOException e) {
                response = new Response(Status.Failure);
            }
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
        }
        if (request instanceof GetFilesListRequest) {
            response = new GetFilesListResponse("server_storage/" + ((GetFilesListRequest) request).getPath());
        }
        if (request instanceof DeleteFileRequest) {
            response = new DeleteFileResponse("server_storage/" + ((DeleteFileRequest) request).getPath());
        }

        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
