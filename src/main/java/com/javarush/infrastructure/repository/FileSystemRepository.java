package com.javarush.infrastructure.repository;

import com.javarush.application.errors.FileProcessingException;
import com.javarush.domain.ports.out.TextRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemRepository implements TextRepository {

    private final Charset charset;
    private final int bufferSize;

    public FileSystemRepository() {
        this(StandardCharsets.UTF_8, 8192);
    }

    public FileSystemRepository(Charset charset, int bufferSize) {
        this.charset = charset;
        this.bufferSize = bufferSize;
    }

    @Override
    public String readAll(String path) {
        Path p = Path.of(path);
        try (BufferedReader reader = Files.newBufferedReader(p, charset)) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[bufferSize];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, read);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new FileProcessingException("Не удалось прочитать файл: " + path, e);
        }
    }

    @Override
    public void writeAll(String path, String content) {
        Path p = Path.of(path);
        try (BufferedWriter writer = Files.newBufferedWriter(p, charset)) {
            writer.write(content);
        } catch (IOException e) {
            throw new FileProcessingException("Не удалось записать файл: " + path, e);
        }
    }

    @Override
    public BufferedReader openReader(String path) {
        Path p = Path.of(path);
        try {
            return Files.newBufferedReader(p, charset);
        } catch (IOException e) {
            throw new FileProcessingException("Не удалось открыть файл для чтения: " + path, e);
        }
    }

    @Override
    public BufferedWriter openWriter(String path) {
        Path p = Path.of(path);
        try {
            return Files.newBufferedWriter(p, charset);
        } catch (IOException e) {
            throw new FileProcessingException("Не удалось открыть файл для записи: " + path, e);
        }
    }

    @Override
    public boolean exists(String path) {
        Path p = Path.of(path);
        try {
            return Files.exists(p);
        } catch (Exception e) {
            throw new FileProcessingException("Ошибка при проверке существования файла: " + path, e);
        }
    }
}
