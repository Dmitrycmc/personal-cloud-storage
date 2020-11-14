package ru.gb.common.messages;

public class DeleteFileRequest extends Request {
    private String path;

    public DeleteFileRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "DeleteFileRequest{" +
                "path='" + path + '\'' +
                "token='" + token + '\'' +
                '}';
    }
}
