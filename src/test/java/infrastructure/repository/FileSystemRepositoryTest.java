package infrastructure.repository;

import com.javarush.infrastructure.repository.FileSystemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileSystemRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void writeAllAndReadAll_roundTrip() {
        FileSystemRepository repo = new FileSystemRepository();

        Path file = tempDir.resolve("test.txt");
        String path = file.toString();
        String content = "Привет, мир!";

        repo.writeAll(path, content);
        assertTrue(repo.exists(path), "Файл должен существовать после записи");

        String read = repo.readAll(path);
        assertEquals(content, read, "Содержимое записанного файла должно совпадать с исходным");
    }

    @Test
    void openReaderAndOpenWriter_workWithBufferedStreams() throws IOException {
        FileSystemRepository repo = new FileSystemRepository();

        Path file = tempDir.resolve("buffered.txt");
        String path = file.toString();
        String content = "Строка для буферизованного ввода/вывода";

        try (BufferedWriter writer = repo.openWriter(path)) {
            writer.write(content);
        }

        assertTrue(repo.exists(path), "Файл должен существовать после записи через openWriter");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = repo.openReader(path)) {
            int ch;
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }
        }

        assertEquals(content, sb.toString(), "Чтение через openReader должно вернуть записанный текст");
    }
}
