package ru.gb.common.messages;

import ru.gb.common.Status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class GetFilesListResponse extends Response{
    private String[] filesList;

    public GetFilesListResponse(String path) {
        try {
            this.status = Status.Success;
            File folder = new File(path);
            System.out.println(path);
            filesList = Arrays.stream(folder.listFiles()).map(file -> file.getName() + (file.isDirectory() ? "/" : "")).toArray(String[]::new);
        } catch (Exception e) {
            this.status = Status.Failure;
            e.printStackTrace();
        }
    }

    public String[] getFilesList() {
        return filesList;
    }

    public String toString() {
        return super.toString() + "\n" +
                "FilesList: " + Arrays.toString(filesList);
    }
}
