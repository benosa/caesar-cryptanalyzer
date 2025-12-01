package com.javarush.application.validators;

import com.javarush.application.errors.InvalidInputException;

public class InputValidator {

    public String requirePath(String raw, String fieldName) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidInputException("Не указан " + fieldName + ".");
        }
        return raw.trim();
    }

    public int parseKey(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidInputException("Ключ не указан.");
        }
        try {
            int key = Integer.parseInt(raw.trim());
            if (key < 0) {
                throw new InvalidInputException("Ключ должен быть неотрицательным.");
            }
            return key;
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Ключ должен быть целым числом.");
        }
    }
}
