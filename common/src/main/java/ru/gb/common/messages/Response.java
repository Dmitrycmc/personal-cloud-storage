package ru.gb.common.messages;

import ru.gb.common.Status;

import java.io.Serializable;

public class Response implements Serializable {
    protected Status status;

    public Response(Status status) {
        this.status = status;
    }

    public Response() {
    }

    public Status getStatus() {
        return status;
    }

    public String toString() {
        return "Status: " + status;
    }
}
