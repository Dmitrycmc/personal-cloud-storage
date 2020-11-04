package ru.gb.common.messages;

import ru.gb.common.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class PostFileRequest extends Request {
    private byte[] data;
    private String fileName;

    public PostFileRequest(String string) throws IOException {
        Path path = Paths.get(string);
        this.fileName = path.getFileName().toString();
        this.data = Files.readAllBytes(path);
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }

    public String toString() {
        return "Filename: " + fileName + "\n" +
                "Data: " + Arrays.toString(data);
    }
}