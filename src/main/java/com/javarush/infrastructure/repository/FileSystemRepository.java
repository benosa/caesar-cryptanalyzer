package com.javarush.infrastructure.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileSystemRepository {

    public String readAll(String path) throws IOException {
        return Files.readString(Path.of(path), StandardCharsets.UTF_8);
    }

    public void writeAll(String path, String content) throws IOException {
        Files.writeString(Path.of(path), content, StandardCharsets.UTF_8);
    }

    public boolean exists(String path) {
        return Files.exists(Path.of(path));
    }
}
