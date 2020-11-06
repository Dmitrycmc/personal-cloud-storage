package ru.gb.common.messages;

import ru.gb.common.Status;

public class LoginResponse extends Response{
    public LoginResponse(String login, String password) {
        if (login.equals("Dima") && password.equals("0000")) {
            this.status = Status.Success;
        } else {
            this.status = Status.Failure;
        }
    }
}
