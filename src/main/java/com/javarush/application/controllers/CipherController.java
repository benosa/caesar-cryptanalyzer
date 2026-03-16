package com.javarush.application.controllers;

import com.javarush.domain.FileService;
import com.javarush.domain.ports.in.CipherService;

public class CipherController {

    private final CipherService fileService;

    public CipherController(CipherService fileService) {
        this.fileService = fileService;
    }

    public void encrypt(String src, String dest, int key) {
        fileService.encryptFile(src, dest, key);
    }

    public void decrypt(String src, String dest, int key) {
        fileService.decryptFile(src, dest, key);
    }

    public int bruteForce(String src, String dest, String samplePath) {
        return fileService.bruteForceDecryptFile(src, dest, samplePath);
    }

    public void statisticalDecrypt(String src, String dest, String samplePath) {
        fileService.statisticalDecryptFile(src, dest, samplePath);
    }
}
