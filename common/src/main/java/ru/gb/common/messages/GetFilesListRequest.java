package ru.gb.common.messages;

public class GetFilesListRequest extends Request {
    private String path;

    public GetFilesListRequest(String path) {
        this.path = path;
    }
    public GetFilesListRequest() {
        this.path = "";
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        return "Path: " + path;
    }
}
