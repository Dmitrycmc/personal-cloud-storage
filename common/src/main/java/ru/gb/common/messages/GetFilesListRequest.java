package ru.gb.common.messages;

public class GetFilesListRequest extends Request {
    private String path;

    public GetFilesListRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "GetFilesListRequest{" +
                "path='" + path + '\'' +
                '}';
    }
}
