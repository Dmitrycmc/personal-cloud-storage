package ru.gb.common.messages;

import ru.gb.common.Status;

import java.io.FileOutputStream;

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
