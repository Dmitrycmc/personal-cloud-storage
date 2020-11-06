package ru.gb.common.messages;

import ru.gb.common.Status;

import java.io.File;

public class PatchFileResponse extends Response{
    public PatchFileResponse(String oldPath, String newPath) {
        try {
            this.status = Status.Success;
            File file1 = new File(oldPath);

            File file2 = new File(newPath);

            if (!file1.exists() || file2.exists() || !file1.renameTo(file2)) {
                throw new Exception();
            }
        } catch (Exception e) {
            this.status = Status.Failure;
        }
    }
}
