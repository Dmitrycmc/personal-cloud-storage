package ru.gb.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Handler extends ChannelInboundHandlerAdapter {
    private enum State {
        FilenameLength, Filename, DataLength, Data
    }

    private ChannelHandlerContext ctx;

    private State state = State.FilenameLength;
    private ByteBuffer buff = ByteBuffer.allocate(8);
    private long bytesCounter = 8;
    private String filename;
    private FileOutputStream fos;

    private void sendBytesArray(byte[] arr) {
        ByteBuf out;
        for (byte b : arr) {
            out = ctx.alloc().buffer(1);
            out.writeByte(b);
            ctx.writeAndFlush(out);
        }
    }

    private void send(long n) {
        ByteBuffer b = ByteBuffer.allocate(8);
        b.putLong(n);
        sendBytesArray(b.array());
    }

    private void send(String s) {
        sendBytesArray(s.getBytes());
    }

    private void send(FileInputStream fis) throws IOException {
        ByteBuf out;
        int b;
        while (true){
            b = fis.read();
            if (b == -1) break;

            out = ctx.alloc().buffer(1);
            out.writeByte(b);
            ctx.writeAndFlush(out);
        }
    }

    void send(Path path) {
        String filename = path.getFileName().toString();
        try {
            FileInputStream fis = new FileInputStream(path.toString());
            send(filename.length());
            send(filename);
            send(fis.getChannel().size());
            send(fis);
            fis.close();
            System.out.println("Успешно передан файл " + filename);
        } catch (IOException e) {
            System.out.println("Ошибка передачи файла " + filename);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        this.ctx = ctx;

        ///send(Paths.get("server_storage/1.txt"));

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
                        }
                        break;
                    case Filename:
                        filename += (char) b;
                        if (bytesCounter == 0) {
                            bytesCounter = 8;
                            state = State.DataLength;
                        }
                        break;
                    case DataLength:
                        buff.put(b);
                        if (bytesCounter == 0) {
                            buff.flip();
                            bytesCounter = buff.getLong();
                            state = State.Data;
                            buff = ByteBuffer.allocate(8);
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
                                System.out.println("File received " + filename);
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
