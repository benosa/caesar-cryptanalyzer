package com.javarush.application.errors;

public class CryptanalyzerException extends RuntimeException {

    public CryptanalyzerException(String message) {
        super(message);
    }

    public CryptanalyzerException(String message, Throwable cause) {
        super(message, cause);
    }
}
