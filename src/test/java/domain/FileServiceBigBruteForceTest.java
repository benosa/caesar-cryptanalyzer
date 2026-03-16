package domain;

import com.javarush.domain.FileService;
import com.javarush.domain.aggregates.Alphabet;
import com.javarush.domain.ports.out.TextRepository;
import com.javarush.infrastructure.repository.FileSystemRepository;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceBigBruteForceTest {

    @Test
    void bruteForce_largeFile_recoversOriginalText_withoutSample() throws Exception {
        Path tempDir = Files.createTempDirectory("crypto-bruteforce-big-");
        Path src = tempDir.resolve("big-source.txt");
        Path enc = tempDir.resolve("big-encrypted.txt");
        Path dec = tempDir.resolve("big-decrypted.txt");

        // Делаем "осмысленный" русский текст, чтобы эвристика имела за что цепляться:
        // много пробелов и типичных букв.
        String phrase = "это очень простой тест брутфорса на большом файле. ";
        int repeat = 10000; // длина ~ несколько сотен килобайт
        StringBuilder sb = new StringBuilder(phrase.length() * repeat);
        for (int i = 0; i < repeat; i++) {
            sb.append(phrase);
        }
        String original = sb.toString();

        Files.writeString(src, original, StandardCharsets.UTF_8);

        TextRepository repo = new FileSystemRepository();
        FileService service = new FileService(repo);

        int key = 17;
        service.encryptFile(src.toString(), enc.toString(), key);

        // Брутфорсим без sample
        service.bruteForceDecryptFile(enc.toString(), dec.toString(), null);

        String decrypted = Files.readString(dec, StandardCharsets.UTF_8);

        assertEquals(original.length(), decrypted.length(), "Длины текстов должны совпадать");
        assertEquals(original, decrypted,
                "Brute force на большом осмысленном тексте должен восстановить исходный текст");
    }
}
