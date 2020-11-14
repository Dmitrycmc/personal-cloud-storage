package ru.gb.common.messages;

import java.io.Serializable;

public class Request implements Serializable {
    protected String token;

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "Response{" +
                "token='" + token + '\'' +
                '}';
    }
}
