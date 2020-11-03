package ru.gb.common.messages;

import java.io.Serializable;

public class GetFileRequest extends Request {
    private String path;

    public GetFileRequest(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
