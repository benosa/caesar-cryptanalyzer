package domain;

import com.javarush.domain.FileService;
import com.javarush.domain.aggregates.Alphabet;
import com.javarush.domain.ports.out.TextRepository;
import com.javarush.infrastructure.repository.FileSystemRepository;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceBigFileTest {

    @Test
    void encryptAndDecrypt_largeFile_producesSameContentAndHash() throws Exception {
        Path tempDir = Files.createTempDirectory("crypto-big-");
        Path src = tempDir.resolve("big-source.txt");
        Path enc = tempDir.resolve("big-encrypted.txt");
        Path dec = tempDir.resolve("big-decrypted.txt");

        // Генерим большой текст ТОЛЬКО из нашего алфавита
        Alphabet alphabet = new Alphabet();
        Random rnd = new Random(42L);

        int length = 2 * 1024 * 1024; // 2М символов
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char c = alphabet.charAt(rnd.nextInt(alphabet.size()));
            sb.append(c);
        }
        String original = sb.toString();

        Files.writeString(
                src,
                original,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        );

        TextRepository repo = new FileSystemRepository();
        FileService service = new FileService(repo);

        int key = 37;
        service.encryptFile(src.toString(), enc.toString(), key);
        service.decryptFile(enc.toString(), dec.toString(), key);

        String decrypted = Files.readString(dec, StandardCharsets.UTF_8);

        // 1) Сначала тупо равенство строк
        assertEquals(original.length(), decrypted.length(), "Длины текстов должны совпадать");
        assertEquals(original, decrypted, "Тексты после шифрования и расшифровки должны совпадать");

        // 2) Потом уже хэши
        byte[] hashOriginal = sha256(original.getBytes(StandardCharsets.UTF_8));
        byte[] hashDecrypted = sha256(decrypted.getBytes(StandardCharsets.UTF_8));

        assertArrayEquals(
                hashOriginal,
                hashDecrypted,
                "Хэш SHA-256 исходного и расшифрованного текста должен совпадать"
        );
    }

    private static byte[] sha256(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(data);
    }
}
