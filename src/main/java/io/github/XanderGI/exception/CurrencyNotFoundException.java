package io.github.XanderGI.exception;

public class CurrencyNotFoundException extends ModelNotFoundException {
    public CurrencyNotFoundException(String message) {
        super(message);
    }
}