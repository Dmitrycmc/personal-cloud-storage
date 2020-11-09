package ru.gb.common;

public enum Commands {
    HELP(),
    EXIT(),

    LOGIN("login, password"),

    POST_USER("login, password"),
    PATCH_USER("newLogin"),
    DELETE_USER(),

    GET_LIST("path"),

    GET("path"),
    POST("path"),
    PATCH("oldPath, newPath"),
    DELETE("path");

    public final String params;

    Commands(String params) {
        this.params = params;
    }

    Commands() {
        this.params = "";
    }
}