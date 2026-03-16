package application.handlers;

import com.javarush.application.controllers.CipherController;
import com.javarush.application.handlers.CliApplication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CliApplicationTest {

    @Mock
    CipherController cipherController;

    @Test
    void run_encryptThenExit_callsControllerEncrypt() {
        // последовательность ввода:
        // 1 (шифрование)
        // src.txt
        // dest.txt
        // 5 (ключ)
        // 0 (выход)
        String input = String.join(System.lineSeparator(),
                "1",
                "src.txt",
                "dest.txt",
                "5",
                "0"
        ) + System.lineSeparator();

        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(outContent));

            CliApplication app = new CliApplication(cipherController);
            app.run();

            verify(cipherController).encrypt("src.txt", "dest.txt", 5);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }

    @Test
    void run_unknownMenuItemPrintsError() {
        // 9 — неизвестный пункт, потом 0 — выход
        String input = String.join(System.lineSeparator(),
                "9",
                "0"
        ) + System.lineSeparator();

        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();

        try {
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(outContent));

            CliApplication app = new CliApplication(cipherController);
            app.run();

            String output = outContent.toString(StandardCharsets.UTF_8);
            assertTrue(output.contains("Неизвестный пункт меню."),
                    "При неизвестном пункте меню должно выводиться сообщение об ошибке");
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
