package com.javarush.domain;

import com.javarush.domain.aggregates.Alphabet;
import com.javarush.domain.ports.out.TextRepository;

public class FileService {

    private final TextRepository textRepository;
    private final Alphabet alphabet = new Alphabet();

    public FileService(TextRepository textRepository) {
        this.textRepository = textRepository;
    }

    public void encryptFile(String sourcePath, String destPath, int key) {
        validatePaths(sourcePath, destPath);
        validateKey(key);

        String text = textRepository.readText(sourcePath);
        String encrypted = encryptText(text, key);
        textRepository.writeText(destPath, encrypted);
    }

    public void decryptFile(String sourcePath, String destPath, int key) {
        validatePaths(sourcePath, destPath);
        validateKey(key);

        String text = textRepository.readText(sourcePath);
        String decrypted = decryptText(text, key);
        textRepository.writeText(destPath, decrypted);
    }

    private String encryptText(String text, int key) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char ch : text.toCharArray()) {
            // приведение к нижнему, если работаешь в нижнем регистре
            char lower = Character.toLowerCase(ch);
            char shifted = alphabet.shift(lower, key);
            sb.append(shifted);
        }
        return sb.toString();
    }

    private String decryptText(String text, int key) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char ch : text.toCharArray()) {
            char lower = Character.toLowerCase(ch);
            char shifted = alphabet.unshift(lower, key);
            sb.append(shifted);
        }
        return sb.toString();
    }

    private void validatePaths(String sourcePath, String destPath) {
        if (sourcePath == null || sourcePath.isBlank()) {
            throw new IllegalArgumentException("Не указан путь к исходному файлу");
        }
        if (destPath == null || destPath.isBlank()) {
            throw new IllegalArgumentException("Не указан путь к выходному файлу");
        }
        if (!textRepository.exists(sourcePath)) {
            throw new IllegalArgumentException("Исходный файл не существует: " + sourcePath);
        }
    }

    private void validateKey(int key) {
        if (key < 0) {
            throw new IllegalArgumentException("Ключ должен быть неотрицательным");
        }
        // можно ужать ключ по модулю алфавита
    }
}
