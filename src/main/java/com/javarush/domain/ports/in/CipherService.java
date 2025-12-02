package com.javarush.domain.ports.in;

public interface CipherService {

    void encryptFile(String sourcePath, String destPath, int key);

    void decryptFile(String sourcePath, String destPath, int key);

    int bruteForceDecryptFile(String sourcePath, String destPath, String samplePath);

    void statisticalDecryptFile(String sourcePath, String destPath, String samplePath);
}
