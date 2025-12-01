package com.javarush.application.controllers;

import com.javarush.domain.FileService;

public class CipherController {

    private final FileService fileService;

    public CipherController(FileService fileService) {
        this.fileService = fileService;
    }

    public void encrypt(String src, String dest, int key) {
        try {
            fileService.encryptFile(src, dest, key);
            System.out.println("Шифрование завершено успешно.");
        } catch (Exception e) {
            System.err.println("Ошибка при шифровании: " + e.getMessage());
        }
    }

    public void decrypt(String src, String dest, int key) {
        try {
            fileService.decryptFile(src, dest, key);
            System.out.println("Расшифровка завершена успешно.");
        } catch (Exception e) {
            System.err.println("Ошибка при расшифровке: " + e.getMessage());
        }
    }

    public void bruteForce(String src, String dest, String samplePath) {
        try {
            fileService.bruteForceDecryptFile(src, dest, samplePath);
            System.out.println("Brute force расшифровка завершена. Результат записан в файл: " + dest);
        } catch (Exception e) {
            System.err.println("Ошибка при brute force расшифровке: " + e.getMessage());
        }
    }
}
