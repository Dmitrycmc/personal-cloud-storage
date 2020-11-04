package ru.gb.common.messages;

public class GetFileRequest extends Request {
    private String path;

    public GetFileRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "GetFileRequest{" +
                "path='" + path + '\'' +
                '}';
    }
}
