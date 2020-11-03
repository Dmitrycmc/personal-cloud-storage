package ru.gb.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import ru.gb.common.Commands;
import ru.gb.common.FileReceiver;
import ru.gb.common.StringReceiver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

public class Handler extends ChannelInboundHandlerAdapter {
    private Commands command;
    private boolean idle = true;

    private ChannelHandlerContext ctx;
    private FileReceiver fileReceiver = new FileReceiver("server_storage");
    private StringReceiver stringReceiver = new StringReceiver();

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

    private void send(byte b) {
        ByteBuf out = ctx.alloc().buffer(1);
        out.writeByte(b);
        ctx.writeAndFlush(out);
    }

    private void send(String s) {
        send((byte)s.length());
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
        try {
            ByteBuf in = (ByteBuf) msg;
            while (in.isReadable()) {
                byte b = in.readByte();
                if (idle) {
                    Optional<Commands> matchedCommand = Arrays.stream(Commands.class.getEnumConstants()).filter(c -> c.code == b).findAny();
                    if (!matchedCommand.isPresent()) {
                        System.out.println("Wrong command");
                        return;
                    }
                    idle = false;
                    command = matchedCommand.get();
                    System.out.println(command);
                } else {
                    switch (command) {
                        case POST_FILES:
                            fileReceiver.put(b);
                            if (fileReceiver.fileIsReceived()) {
                                idle = true;
                            }
                            break;
                        case GET_FILES:
                            stringReceiver.put(b);
                            if (stringReceiver.received()) {
                                send(Paths.get("server_storage/" + stringReceiver));
                                idle = true;
                            }
                            break;
                        case GET_FILES_LIST:
                            stringReceiver.put(b);
                            if (stringReceiver.received()) {
                                StringBuilder sb = new StringBuilder();
                                File folder = new File("server_storage/"+stringReceiver);
                                for (final File fileEntry : folder.listFiles()) {
                                    if (fileEntry.isDirectory()) {
                                        sb.append(fileEntry.getName()).append("/\n");
                                    } else {
                                        sb.append(fileEntry.getName()).append('\n');
                                    }
                                }
                                send(sb.toString());
                                idle = true;
                            }
                            break;
                        default:

                    }
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
