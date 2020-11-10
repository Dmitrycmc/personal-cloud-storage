package ru.gb.common.messages;

import ru.gb.common.Status;

import java.io.Serializable;

public class Response implements Serializable {
    protected Status status;

    public Response(Status status) {
        this.status = status;
    }

    public Response(boolean successFlag) {
        this.status = successFlag ? Status.Success : Status.Failure;
    }

    public Response() {
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status=" + status +
                '}';
    }
}
