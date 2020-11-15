package ru.gb.common.messages;

import java.nio.file.Paths;

public class PostFileRequest extends Request {
    private String fileName;

    public PostFileRequest(String string) {
        this.fileName = Paths.get(string).getFileName().toString();
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "PostFileRequest{" +
                "fileName='" + fileName + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}