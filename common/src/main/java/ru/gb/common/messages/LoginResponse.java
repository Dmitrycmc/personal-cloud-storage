package ru.gb.common.messages;

import ru.gb.common.Status;

public class LoginResponse extends Response{
    private String token;

    public LoginResponse(String token) {
        this.token = token;
        this.status = Status.Success;
    }

    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                ", status=" + status +
                "token='" + token + '\'' +
                '}';
    }
}
