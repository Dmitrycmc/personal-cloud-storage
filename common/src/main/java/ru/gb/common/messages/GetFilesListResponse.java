package ru.gb.common.messages;

import ru.gb.common.Status;

import java.util.Arrays;

public class GetFilesListResponse extends Response{
    private String[] filesList;

    public GetFilesListResponse(String[] filesList) {
        this.filesList = filesList;
        this.status = filesList != null ? Status.Success : Status.Failure;
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
