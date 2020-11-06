package ru.gb.common.messages;

public class LoginRequest extends Request {
    private String login;
    private String password;

    public LoginRequest(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "DeleteFileRequest{" +
                "login='" + login + '\'' +
                "password='" + password + '\'' +
                '}';
    }
}
