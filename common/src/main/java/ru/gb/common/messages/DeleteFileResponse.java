package ru.gb.common.messages;

import ru.gb.common.Status;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class DeleteFileResponse extends Response{
    public DeleteFileResponse(String path) {
        try {
            this.status = Status.Success;
            Files.delete(Paths.get(path));
        } catch (Exception e) {
            this.status = Status.Failure;
        }
    }
}
