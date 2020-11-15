package ru.gb.common.messages;

public class CreateUserRequest extends LoginRequest {
    public CreateUserRequest(String login, String password) {
        super(login, password);
    }

    @Override
    public String toString() {
        return "CreateUserRequest{" +
                "login='" + login + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
