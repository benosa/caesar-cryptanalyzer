package com.javarush.application.errors;

public class FileProcessingException extends CryptanalyzerException {

    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
