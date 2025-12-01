package com.javarush.application.controllers;

import com.javarush.domain.FileService;

public class CipherController {

    private final FileService fileService;

    public CipherController(FileService fileService) {
        this.fileService = fileService;
    }

    public void encrypt(String sourcePath, String destPath, int key) {
        validatePaths(sourcePath, destPath);
        validateKey(key);
        try {
            fileService.encrypt(sourcePath, destPath, key);
            System.out.println("Шифрование завершено успешно.");
        } catch (Exception e) {
            System.err.println("Ошибка при шифровании: " + e.getMessage());
        }
    }

    public void decrypt(String sourcePath, String destPath, int key) {
        validatePaths(sourcePath, destPath);
        validateKey(key);
        try {
            fileService.decrypt(sourcePath, destPath, key);
            System.out.println("Расшифровка завершена успешно.");
        } catch (Exception e) {
            System.err.println("Ошибка при расшифровке: " + e.getMessage());
        }
    }

    // сюда же потом добавишь методы bruteForce(), statisticalDecrypt()

    private void validatePaths(String sourcePath, String destPath) {
        if (sourcePath == null || sourcePath.isBlank()) {
            throw new IllegalArgumentException("Не указан путь к исходному файлу");
        }
        if (destPath == null || destPath.isBlank()) {
            throw new IllegalArgumentException("Не указан путь к выходному файлу");
        }
    }

    private void validateKey(int key) {
        if (key < 0) {
            throw new IllegalArgumentException("Ключ должен быть неотрицательным");
        }
        // дальше можешь привязать к размеру алфавита
    }
}
