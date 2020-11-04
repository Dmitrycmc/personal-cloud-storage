package ru.gb.common.messages;

import ru.gb.common.Status;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class PostFileResponse extends Response{
    public PostFileResponse(String path, byte[] data) {
        try {
            this.status = Status.Success;
            FileOutputStream fos = new FileOutputStream(path, true);
            fos.write(data);
            fos.close();
        } catch (Exception e) {
            this.status = Status.Failure;
            e.printStackTrace();
        }
    }
}
