package ru.gb.common.messages;

public class PatchFileRequest extends Request {
    private String oldPath;
    private String newPath;

    public PatchFileRequest(String oldPath, String newPath) {
        this.oldPath = oldPath;
        this.newPath = newPath;
    }

    public String getOldPath() {
        return oldPath;
    }
    public String getNewPath() {
        return newPath;
    }

    @Override
    public String toString() {
        return "PatchFileRequest{" +
                "oldPath='" + oldPath + '\'' +
                "newPath='" + newPath + '\'' +
                '}';
    }
}
