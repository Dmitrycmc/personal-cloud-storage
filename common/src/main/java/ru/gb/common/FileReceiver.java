package ru.gb.common;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class FileReceiver {
    private enum State {
        Filename, DataLength, Data
    }

    private String pathPrefix;
    private boolean idle = true;
    private State state = State.Filename;
    private StringReceiver stringReceiver = new StringReceiver();
    private ByteBuffer buff = ByteBuffer.allocate(8);
    private long bytesCounter = 8;
    private FileOutputStream fos;

    public FileReceiver(String pathPrefix) {
        this.pathPrefix = pathPrefix;
    }

    public void put(byte b) {
        idle = false;
        bytesCounter--;
        switch (state) {
            case Filename:
                stringReceiver.put(b);
                if (stringReceiver.received()) {
                    bytesCounter = 8;
                    state = State.DataLength;
                    buff = ByteBuffer.allocate(8);
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
                        fos = new FileOutputStream(pathPrefix + "/" + stringReceiver);
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
                        state = State.Filename;
                        fos.close();
                        System.out.println("File received " + stringReceiver);
                        idle = true;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    public boolean fileIsReceived() {
        return idle;
    }
}
