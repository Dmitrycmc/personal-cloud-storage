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
    private ByteBuffer buff = ByteBuffer.allocate(8);
    private long c = 8;
    private String filename;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf in = (ByteBuf) msg;
            while (in.isReadable()) {
                c--;
                byte b = in.readByte();
                switch (state) {
                    case FilenameLength:
                        buff.put(b);
                        if (c == 0) {
                            filename = "";
                            buff.flip();
                            c = buff.getLong();
                            state = State.Filename;
                            buff = ByteBuffer.allocate(8);
                            System.out.println("FilenameLength = " + c);
                        }
                        break;
                    case Filename:
                        filename += (char) b;
                        if (c == 0) {
                            c = 8;
                            state = State.DataLength;
                            System.out.println("Filename = " + filename);
                        }
                        break;
                    case DataLength:
                        buff.put(b);
                        if (c == 0) {
                            buff.flip();
                            c = buff.getLong();
                            state = State.Data;
                            buff = ByteBuffer.allocate(8);
                            System.out.println("DataLength = " + c);
                        }
                        break;
                    case Data:
                        try (FileOutputStream fos = new FileOutputStream("server_storage/" + filename, true)) {
                            fos.write(b);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (c == 0) {
                            c = 8;
                            state = State.FilenameLength;
                            System.out.println("Data written");
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