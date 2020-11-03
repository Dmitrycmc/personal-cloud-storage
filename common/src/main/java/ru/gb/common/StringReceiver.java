package ru.gb.common;

public class StringReceiver {
    private enum State {
        Length, String
    }

    private boolean idle = true;
    private State state = State.Length;
    private byte bytesCounter = 1;
    private String string;

    public void put(byte b) {
        idle = false;
        bytesCounter--;
        switch (state) {
            case Length:
                bytesCounter = b;
                string = "";
                state = State.String;
                break;
            case String:
                string += (char) b;
                if (bytesCounter == 0) {
                    bytesCounter = 1;
                    state = State.Length;
                    idle = true;
                }
                break;
        }
    }

    public boolean received() {
        return idle;
    }

    public String toString() {
        return string;
    }
}
