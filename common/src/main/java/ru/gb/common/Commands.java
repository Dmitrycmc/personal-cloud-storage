package ru.gb.common;

public enum Commands {
    HELP(),
    EXIT(),

    LOGIN("login, password"),

    POST_USER("login, password"),
    PATCH_USER("newLogin"),
    DELETE_USER(),

    GET_FILES_LIST("path"),

    GET_FILE("path"),
    POST_FILE("path"),
    PATCH_FILE("oldPath, newPath"),
    DELETE_FILE("path");

    public final String params;

    Commands(String params) {
        this.params = params;
    }

    Commands() {
        this.params = "";
    }
}