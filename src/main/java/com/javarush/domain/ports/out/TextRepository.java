package com.javarush.domain.ports.out;

import java.io.BufferedReader;
import java.io.BufferedWriter;

public interface TextRepository {

    // Для режимов, которым нужен весь текст (brute force, статистика)
    String readAll(String path);

    void writeAll(String path, String content);

    // Для потоковой обработки больших файлов (encrypt/decrypt)
    BufferedReader openReader(String path);

    BufferedWriter openWriter(String path);

    boolean exists(String path);
}
