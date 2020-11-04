package ru.gb.common.messages;

import ru.gb.common.Status;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GetFileResponse extends Response{
    private String fileName;

    public GetFileResponse(String string) {
        Path path = Paths.get(string);
        File f = new File(string);
        if (f.exists() && !f.isDirectory()) {
            this.status = Status.Success;
            this.fileName = path.getFileName().toString();
        } else {
            this.status = Status.Failure;
        }
    }

    public String getFileName() {
        return fileName;
    }

    @Override
    public String toString() {
        return "GetFileResponse{" +
                "fileName='" + fileName + '\'' +
                ", status=" + status +
                '}';
    }
}
