package domain;

import com.javarush.application.errors.FileProcessingException;
import com.javarush.application.errors.InvalidInputException;
import com.javarush.domain.FileService;
import com.javarush.domain.ports.out.TextRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @Mock
    TextRepository textRepository;

    // ===== encryptFile / decryptFile =====

    @Test
    void encryptFile_withKeyZero_preservesText() throws Exception {
        FileService service = new FileService(textRepository);

        String sourcePath = "src.txt";
        String destPath = "dest.txt";
        String original = "Привет, мир!";

        when(textRepository.exists(sourcePath)).thenReturn(true);
        when(textRepository.openReader(sourcePath))
                .thenReturn(new BufferedReader(new StringReader(original)));

        StringWriter out = new StringWriter();
        when(textRepository.openWriter(destPath))
                .thenReturn(new BufferedWriter(out));

        service.encryptFile(sourcePath, destPath, 0);

        assertEquals(original, out.toString(), "При ключе 0 текст должен остаться неизменным");
    }

    @Test
    void encryptFile_throwsInvalidInputWhenSourceMissing() {
        FileService service = new FileService(textRepository);

        String sourcePath = "missing.txt";
        String destPath = "dest.txt";

        when(textRepository.exists(sourcePath)).thenReturn(false);

        assertThrows(InvalidInputException.class,
                () -> service.encryptFile(sourcePath, destPath, 1),
                "Должно кидаться исключение, если исходный файл не существует");

        verify(textRepository, never()).openReader(anyString());
        verify(textRepository, never()).openWriter(anyString());
    }

    @Test
    void encryptFile_throwsInvalidInputWhenDestBlank() {
        FileService service = new FileService(textRepository);

        String sourcePath = "src.txt";

        when(textRepository.exists(sourcePath)).thenReturn(true);

        assertThrows(InvalidInputException.class,
                () -> service.encryptFile(sourcePath, "   ", 1),
                "Должно кидаться исключение, если путь к выходному файлу пустой/пробельный");
    }

    @Test
    void encryptFile_throwsInvalidInputWhenKeyNegative() {
        FileService service = new FileService(textRepository);

        String sourcePath = "src.txt";
        String destPath = "dest.txt";

        when(textRepository.exists(sourcePath)).thenReturn(true);

        assertThrows(InvalidInputException.class,
                () -> service.encryptFile(sourcePath, destPath, -1),
                "Отрицательный ключ должен приводить к InvalidInputException");
    }

    @Test
    void encryptFile_wrapsIoExceptionsIntoFileProcessingException() throws Exception {
        FileService service = new FileService(textRepository);

        String sourcePath = "src.txt";
        String destPath = "dest.txt";

        when(textRepository.exists(sourcePath)).thenReturn(true);

        // Мокаем ридер, у которого read() кидает IOException
        BufferedReader brokenReader = mock(BufferedReader.class);
        when(textRepository.openReader(sourcePath)).thenReturn(brokenReader);

        StringWriter out = new StringWriter();
        when(textRepository.openWriter(destPath))
                .thenReturn(new BufferedWriter(out));

        when(brokenReader.read()).thenThrow(new IOException("IO error"));

        assertThrows(FileProcessingException.class,
                () -> service.encryptFile(sourcePath, destPath, 1),
                "IO-ошибка при чтении должна оборачиваться в FileProcessingException");
    }

    @Test
    void decryptFile_withKeyZero_preservesText() throws Exception {
        FileService service = new FileService(textRepository);

        String sourcePath = "enc.txt";
        String destPath = "dec.txt";
        String original = "Тестовый текст...";

        when(textRepository.exists(sourcePath)).thenReturn(true);
        when(textRepository.openReader(sourcePath))
                .thenReturn(new BufferedReader(new StringReader(original)));

        StringWriter out = new StringWriter();
        when(textRepository.openWriter(destPath))
                .thenReturn(new BufferedWriter(out));

        service.decryptFile(sourcePath, destPath, 0);

        assertEquals(original, out.toString(), "При ключе 0 расшифровка должна вернуть исходный текст");
    }

    // ===== bruteForceDecryptFile =====

    @Test
    void bruteForceDecryptFile_throwsInvalidInputWhenSourceMissing() {
        FileService service = new FileService(textRepository);

        String sourcePath = "missing.txt";
        String destPath = "out.txt";

        when(textRepository.exists(sourcePath)).thenReturn(false);

        assertThrows(InvalidInputException.class,
                () -> service.bruteForceDecryptFile(sourcePath, destPath, null),
                "Если исходный файл не существует, должен быть InvalidInputException");
    }

    @Test
    void bruteForceDecryptFile_throwsInvalidInputWhenSampleNotExists() {
        FileService service = new FileService(textRepository);

        String sourcePath = "enc.txt";
        String destPath = "out.txt";
        String samplePath = "sample.txt";

        when(textRepository.exists(sourcePath)).thenReturn(true);
        when(textRepository.exists(samplePath)).thenReturn(false);

        assertThrows(InvalidInputException.class,
                () -> service.bruteForceDecryptFile(sourcePath, destPath, samplePath),
                "Если репрезентативный файл не существует, должен быть InvalidInputException");
    }

    @Test
    void bruteForceDecryptFile_writesSomeResultToDest() {
        FileService service = new FileService(textRepository);

        String sourcePath = "enc.txt";
        String destPath = "out.txt";

        when(textRepository.exists(sourcePath)).thenReturn(true);
        when(textRepository.readAll(sourcePath))
                .thenReturn("зашифрованный текст");

        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

        service.bruteForceDecryptFile(sourcePath, destPath, null);

        verify(textRepository).writeAll(eq(destPath), contentCaptor.capture());
        assertNotNull(contentCaptor.getValue(), "Результат brute force не должен быть null");
        assertFalse(contentCaptor.getValue().isEmpty(), "Результат brute force не должен быть пустым");
    }

    // ===== statisticalDecryptFile =====

    @Test
    void statisticalDecryptFile_throwsInvalidInputWhenSampleNotProvided() {
        FileService service = new FileService(textRepository);

        String sourcePath = "enc.txt";
        String destPath = "out.txt";

        when(textRepository.exists(sourcePath)).thenReturn(true);

        assertThrows(InvalidInputException.class,
                () -> service.statisticalDecryptFile(sourcePath, destPath, null),
                "Для статистического анализа должен быть указан samplePath");
    }

    @Test
    void statisticalDecryptFile_throwsInvalidInputWhenSampleMissing() {
        FileService service = new FileService(textRepository);

        String sourcePath = "enc.txt";
        String destPath = "out.txt";
        String samplePath = "sample.txt";

        when(textRepository.exists(sourcePath)).thenReturn(true);
        when(textRepository.exists(samplePath)).thenReturn(false);

        assertThrows(InvalidInputException.class,
                () -> service.statisticalDecryptFile(sourcePath, destPath, samplePath),
                "Если репрезентативный файл не существует, должен быть InvalidInputException");
    }

    @Test
    void statisticalDecryptFile_writesResultToDest() {
        FileService service = new FileService(textRepository);

        String sourcePath = "enc.txt";
        String destPath = "out.txt";
        String samplePath = "sample.txt";

        when(textRepository.exists(sourcePath)).thenReturn(true);
        when(textRepository.exists(samplePath)).thenReturn(true);

        when(textRepository.readAll(sourcePath)).thenReturn("зашифрованный текст");
        when(textRepository.readAll(samplePath)).thenReturn("пример обычного текста");

        ArgumentCaptor<String> contentCaptor = ArgumentCaptor.forClass(String.class);

        service.statisticalDecryptFile(sourcePath, destPath, samplePath);

        verify(textRepository).writeAll(eq(destPath), contentCaptor.capture());
        assertNotNull(contentCaptor.getValue(), "Результат статистического анализа не должен быть null");
        assertFalse(contentCaptor.getValue().isEmpty(), "Результат статистического анализа не должен быть пустым");
    }
}
