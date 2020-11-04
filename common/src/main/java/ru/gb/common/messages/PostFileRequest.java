package ru.gb.common.messages;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PostFileRequest extends Request {
    private String fileName;

    public PostFileRequest(String string) {
        Path path = Paths.get(string);
        this.fileName = path.getFileName().toString();
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "PostFileRequest{" +
                "fileName='" + fileName + '\'' +
                '}';
    }
}