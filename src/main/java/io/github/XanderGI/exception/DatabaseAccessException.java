package io.github.XanderGI.exception;

public class DatabaseAccessException extends RuntimeException {
    public DatabaseAccessException(String message) {
        super(message);
    }

    public DatabaseAccessException(Throwable cause) {
        super(cause);
    }
}