package com.javarush.application.errors;

public class InvalidInputException extends CryptanalyzerException {

    public InvalidInputException(String message) {
        super(message);
    }
}
