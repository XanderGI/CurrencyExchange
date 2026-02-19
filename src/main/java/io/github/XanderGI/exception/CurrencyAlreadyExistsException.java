package io.github.XanderGI.exception;

public class CurrencyAlreadyExistsException extends ModelAlreadyExistsException {
    public CurrencyAlreadyExistsException(String message) {
        super(message);
    }
}