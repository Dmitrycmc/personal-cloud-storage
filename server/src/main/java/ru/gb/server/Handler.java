package ru.gb.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Handler extends ChannelInboundHandlerAdapter {
    private enum State {
        FilenameLength, Filename, DataLength, Data
    }

    private State state = State.FilenameLength;
    private ByteBuffer buff = ByteBuffer.allocate(4);
    private int c = 4;
    private String filename;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
            while (in.isReadable()) {
                c--;
                switch (state) {
                    case FilenameLength:
                        buff.put(in.readByte());
                        if (c == 0) {
                            filename = "";
                            buff.flip();
                            c = buff.getInt();
                            state = State.Filename;
                            buff = ByteBuffer.allocate(4);
                        }
                        break;
                    case Filename:
                        filename += (char) in.readByte();
                        if (c == 0) {
                            c = 4;
                            state = State.DataLength;
                        }
                        break;
                    case DataLength:
                        buff.put(in.readByte());
                        if (c == 0) {
                            buff.flip();
                            c = buff.getInt();
                            state = State.Data;
                            buff = ByteBuffer.allocate(4);
                        }
                        break;
                    case Data:
                        try (FileOutputStream fos = new FileOutputStream("server_storage/" + filename, true)) {
                            fos.write(in.readByte());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (c == 0) {
                            c = 4;
                            state = State.FilenameLength;
                        }
                        break;
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
