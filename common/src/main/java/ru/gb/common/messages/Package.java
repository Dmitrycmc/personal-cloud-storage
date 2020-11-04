package ru.gb.common.messages;

import java.io.Serializable;
import java.util.Arrays;

public class Package implements Serializable {
    private byte[] data;
    private boolean terminate;

    public Package(byte[] data, boolean terminate) {
        this.data = data;
        this.terminate = terminate;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isTerminate() {
        return terminate;
    }

    @Override
    public String toString() {
        return "Package{" +
                "data.length=" + data.length +
                ", terminate=" + terminate +
                '}';
    }
}
