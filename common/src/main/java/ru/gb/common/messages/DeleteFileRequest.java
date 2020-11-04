package ru.gb.common.messages;

public class DeleteFileRequest extends Request {
    private String path;

    public DeleteFileRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        return "Path: " + path;
    }
}
