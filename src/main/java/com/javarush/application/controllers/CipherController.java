package com.javarush.application.controllers;

import com.javarush.domain.FileService;

public class CipherController {

    private final FileService fileService;

    public CipherController(FileService fileService) {
        this.fileService = fileService;
    }

    public void encrypt(String src, String dest, int key) {
        fileService.encryptFile(src, dest, key);
    }

    public void decrypt(String src, String dest, int key) {
        fileService.decryptFile(src, dest, key);
    }

    public void bruteForce(String src, String dest, String samplePath) {
        fileService.bruteForceDecryptFile(src, dest, samplePath);
    }

    public void statisticalDecrypt(String src, String dest, String samplePath) {
        fileService.statisticalDecryptFile(src, dest, samplePath);
    }
}
