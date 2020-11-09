package ru.gb.client.exceptions;

public class InvalidArgumentsNumberException extends Exception {
    private int requiredArgumentsNumber;

    public InvalidArgumentsNumberException(int requiredArgumentsNumber) {
        this.requiredArgumentsNumber = requiredArgumentsNumber;
    }

    public int getRequiredArgumentsNumber() {
        return requiredArgumentsNumber;
    }
}
