package ru.gb.common.messages;

import ru.gb.common.Status;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class GetFileResponse extends Response{
    private byte[] data;
    private String fileName;

    public GetFileResponse(String string) {
        Path path = Paths.get(string);
        try {
            this.status = Status.Success;
            this.fileName = path.getFileName().toString();
            this.data = Files.readAllBytes(path);
        } catch (IOException e) {
            this.status = Status.Failure;
        }
    }

    public byte[] getData() {
        return data;
    }

    public String getFileName() {
        return fileName;
    }

    public String toString() {
        return super.toString() + "\n" +
                "Filename: " + fileName + "\n" +
                "Data: " + Arrays.toString(data);
    }
}
