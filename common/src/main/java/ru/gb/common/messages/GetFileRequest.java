package ru.gb.common.messages;

public class GetFileRequest extends Request {
    private String path;

    public GetFileRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        return "Path: " + path;
    }
}
