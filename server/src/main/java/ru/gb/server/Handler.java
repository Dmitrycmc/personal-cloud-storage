package ru.gb.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import ru.gb.common.messages.*;

public class Handler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object request) {
        System.out.println(request);
        Response response = null;

        if (request instanceof GetFileRequest) {
            response = new GetFileResponse("server_storage/" + ((GetFileRequest) request).getPath());
        }
        if (request instanceof PostFileRequest) {
            response = new PostFileResponse("server_storage/" + ((PostFileRequest) request).getFileName(), ((PostFileRequest) request).getData());
        }
        if (request instanceof GetFilesListRequest) {
            response = new GetFilesListResponse("server_storage/" + ((GetFilesListRequest) request).getPath());
        }
        if (request instanceof DeleteFileRequest) {
            response = new DeleteFileResponse("server_storage/" + ((DeleteFileRequest) request).getPath());
        }

        System.out.println(response);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
