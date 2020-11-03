package ru.gb.common;

import java.io.Serializable;
import java.util.Arrays;

public class Message implements Serializable {
    private String filename;
    private byte[] data;

    public Message(String filename, byte[] data) {
        this.data = data;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getData() {
        return data;
    }

    public String toString() {
        return "Filename: " + filename + "\nData: " + Arrays.toString(data);
    }
}
