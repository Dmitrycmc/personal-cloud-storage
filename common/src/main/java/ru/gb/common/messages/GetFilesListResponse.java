package ru.gb.common.messages;

import ru.gb.common.Status;

import java.io.File;
import java.util.Arrays;

public class GetFilesListResponse extends Response{
    private String[] filesList;

    public GetFilesListResponse(String path) {
        try {
            this.status = Status.Success;
            File folder = new File(path);
            filesList = Arrays.stream(folder.listFiles()).map(file -> file.getName() + (file.isDirectory() ? "/" : "")).toArray(String[]::new);
        } catch (Exception e) {
            this.status = Status.Failure;
            e.printStackTrace();
        }
    }

    public String[] getFilesList() {
        return filesList;
    }

    @Override
    public String toString() {
        return "GetFilesListResponse{" +
                "filesList=" + Arrays.toString(filesList) +
                ", status=" + status +
                '}';
    }
}
