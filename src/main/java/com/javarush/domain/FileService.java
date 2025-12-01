package com.javarush.domain;

import com.javarush.infrastructure.repository.FileSystemRepository;

import java.io.IOException;

public class FileService {

    private final FileSystemRepository fileRepository;

    public FileService(FileSystemRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public void encrypt(String sourcePath, String destPath, int key) throws IOException {
        String text = fileRepository.readAll(sourcePath);
        String encrypted = encryptText(text, key);
        fileRepository.writeAll(destPath, encrypted);
    }

    public void decrypt(String sourcePath, String destPath, int key) throws IOException {
        String text = fileRepository.readAll(sourcePath);
        String decrypted = decryptText(text, key);
        fileRepository.writeAll(destPath, decrypted);
    }

    // дальше добавишь bruteForce() и statisticalDecrypt()

    private String encryptText(String text, int key) {
        // тут будет логика шифра Цезаря
        return text; // временный заглушка
    }

    private String decryptText(String text, int key) {
        // тут обратный сдвиг
        return text; // временный заглушка
    }
}
