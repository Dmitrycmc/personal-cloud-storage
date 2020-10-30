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
    private long bytesCounter = 8;
    private String filename;
    private FileOutputStream fos;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            ByteBuf in = (ByteBuf) msg;
            while (in.isReadable()) {
                bytesCounter--;
                byte b = in.readByte();
                switch (state) {
                    case FilenameLength:
                        buff.put(b);
                        if (bytesCounter == 0) {
                            filename = "";
                            buff.flip();
                            bytesCounter = buff.getLong();
                            state = State.Filename;
                            buff = ByteBuffer.allocate(8);
                            System.out.println("FilenameLength = " + bytesCounter);
                        }
                        break;
                    case Filename:
                        filename += (char) b;
                        if (bytesCounter == 0) {
                            bytesCounter = 8;
                            state = State.DataLength;
                            System.out.println("Filename = " + filename);
                        }
                        break;
                    case DataLength:
                        buff.put(b);
                        if (bytesCounter == 0) {
                            buff.flip();
                            bytesCounter = buff.getLong();
                            state = State.Data;
                            buff = ByteBuffer.allocate(8);
                            System.out.println("DataLength = " + bytesCounter);
                            try {
                                fos = new FileOutputStream("server_storage/" + filename);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case Data:
                        try {
                            fos.write(b);
                            if (bytesCounter == 0) {
                                bytesCounter = 8;
                                state = State.FilenameLength;
                                fos.close();
                                System.out.println("Data written");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
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
