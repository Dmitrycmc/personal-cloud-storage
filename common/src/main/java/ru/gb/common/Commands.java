package ru.gb.common;

public enum Commands {
    HELP(0),
    LOGIN(10, "login, password"), // {status, token}

    POST_USERS(20, "login, password"), // {status, token}
    PATCH_USERS(21, "newLogin"), // {status}
    DELETE_USERS(22), // {status}

    GET_FILES_LIST(30, "path"), // {status, [path]}

    GET_FILES(40, "[path]"), // {status, [file]}
    POST_FILES(41, "[file]"), // {status}
    PATCH_FILES(42, "[{oldPath, newPath}]"), // {status}
    DELETE_FILES(43, "[path]"); // {status}

    public final int code;
    public final String params;

    Commands(int code, String params) {
        this.code = code;
        this.params = params;
    }

    Commands(int code) {
        this.code = code;
        this.params = "";
    }
}