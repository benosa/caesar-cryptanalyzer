package com.javarush.infrastructure.repository;

import com.javarush.application.errors.FileProcessingException;
import com.javarush.domain.ports.out.TextRepository;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemRepository implements TextRepository {

    @Override
    public String readText(String path) {
        try {
            return Files.readString(Path.of(path), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new FileProcessingException("Не удалось прочитать файл: " + path, e);
        }
    }

    @Override
    public void writeText(String path, String content) {
        try {
            Files.writeString(Path.of(path), content, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new FileProcessingException("Не удалось записать файл: " + path, e);
        }
    }

    @Override
    public boolean exists(String path) {
        try {
            return Files.exists(Path.of(path));
        } catch (Exception e) {
            throw new FileProcessingException("Ошибка при проверке существования файла: " + path, e);
        }
    }
}
