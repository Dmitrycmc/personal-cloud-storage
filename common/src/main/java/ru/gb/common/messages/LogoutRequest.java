package ru.gb.common.messages;

public class LogoutRequest extends Request {
    @Override
    public String toString() {
        return "LogoutRequest{" +
                "token='" + token + '\'' +
                '}';
    }
}
