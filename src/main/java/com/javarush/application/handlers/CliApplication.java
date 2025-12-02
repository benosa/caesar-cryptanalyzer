package com.javarush.application.handlers;

import com.javarush.application.controllers.CipherController;
import com.javarush.application.errors.CryptanalyzerException;
import com.javarush.application.errors.FileProcessingException;
import com.javarush.application.errors.InvalidInputException;
import com.javarush.application.validators.InputValidator;
import picocli.CommandLine.Command;

import java.util.Scanner;

@Command(
        name = "cypher",
        description = "Caesar cipher CLI application",
        mixinStandardHelpOptions = true
)
public class CliApplication implements Runnable {

    private final CipherController cipherController;
    private final InputValidator inputValidator = new InputValidator();

    public CliApplication(CipherController cipherController) {
        this.cipherController = cipherController;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> safeEncrypt(scanner);
                case "2" -> safeDecrypt(scanner);
                case "3" -> safeBruteForce(scanner);
                case "4" -> safeStatistical(scanner);
                case "0" -> {
                    System.out.println("Выход.");
                    return;
                }
                default -> System.out.println("Неизвестный пункт меню.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("=== Caesar Cryptanalyzer ===");
        System.out.println("1. Шифрование файла");
        System.out.println("2. Расшифровка файла с ключом");
        System.out.println("3. Brute force");
        System.out.println("4. Статистический анализ");
        System.out.println("0. Выход");
        System.out.print("Выбор: ");
    }

    private void safeEncrypt(Scanner scanner) {
        try {
            handleEncrypt(scanner);
            System.out.println("Шифрование завершено успешно.");
        } catch (InvalidInputException e) {
            System.err.println("[INPUT ERROR] " + e.getMessage());
        } catch (FileProcessingException e) {
            System.err.println("[FILE ERROR] " + e.getMessage());
        } catch (CryptanalyzerException e) {
            System.err.println("[ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[INTERNAL ERROR] " + e.getMessage());
        }
    }


    private void safeDecrypt(Scanner scanner) {
        try {
            handleDecrypt(scanner);
            System.out.println("Расшифровка завершена успешно.");
        } catch (InvalidInputException e) {
            System.err.println("[INPUT ERROR] " + e.getMessage());
        } catch (FileProcessingException e) {
            System.err.println("[FILE ERROR] " + e.getMessage());
        } catch (CryptanalyzerException e) {
            System.err.println("[ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[INTERNAL ERROR] " + e.getMessage());
        }
    }

    private void safeBruteForce(Scanner scanner) {
        try {
            handleBruteForce(scanner);
            System.out.println("Brute force расшифровка завершена.");
        } catch (InvalidInputException e) {
            System.err.println("[INPUT ERROR] " + e.getMessage());
        } catch (FileProcessingException e) {
            System.err.println("[FILE ERROR] " + e.getMessage());
        } catch (CryptanalyzerException e) {
            System.err.println("[ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[INTERNAL ERROR] " + e.getMessage());
        }
    }

    private void safeStatistical(Scanner scanner) {
        try {
            handleStatistical(scanner);
            System.out.println("Статистическая расшифровка завершена.");
        } catch (InvalidInputException e) {
            System.err.println("[INPUT ERROR] " + e.getMessage());
        } catch (FileProcessingException e) {
            System.err.println("[FILE ERROR] " + e.getMessage());
        } catch (CryptanalyzerException e) {
            System.err.println("[ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[INTERNAL ERROR] " + e.getMessage());
        }
    }

    private void handleEncrypt(Scanner scanner) {
        System.out.print("Путь к исходному файлу: ");
        String src = inputValidator.requirePath(scanner.nextLine(), "путь к исходному файлу");

        System.out.print("Путь к выходному файлу: ");
        String dest = inputValidator.requirePath(scanner.nextLine(), "путь к выходному файлу");

        System.out.print("Ключ (целое число): ");
        int key = inputValidator.parseKey(scanner.nextLine());

        cipherController.encrypt(src, dest, key);
    }

    private void handleDecrypt(Scanner scanner) {
        System.out.print("Путь к зашифрованному файлу: ");
        String src = inputValidator.requirePath(scanner.nextLine(), "путь к зашифрованному файлу");

        System.out.print("Путь к выходному файлу: ");
        String dest = inputValidator.requirePath(scanner.nextLine(), "путь к выходному файлу");

        System.out.print("Ключ (целое число): ");
        int key = inputValidator.parseKey(scanner.nextLine());

        cipherController.decrypt(src, dest, key);
    }

    private void handleBruteForce(Scanner scanner) {
        System.out.print("Путь к зашифрованному файлу: ");
        String src = inputValidator.requirePath(scanner.nextLine(), "путь к зашифрованному файлу");

        System.out.print("Путь к выходному файлу: ");
        String dest = inputValidator.requirePath(scanner.nextLine(), "путь к выходному файлу");

        System.out.print("Путь к репрезентативному файлу (Enter, если нет): ");
        String sample = scanner.nextLine();
        if (sample != null && sample.isBlank()) {
            sample = null;
        }

        int key = cipherController.bruteForce(src, dest, sample);

        System.out.println("Ключ (целое число): " + key);
    }

    private void handleStatistical(Scanner scanner) {
        System.out.print("Путь к зашифрованному файлу: ");
        String src = inputValidator.requirePath(scanner.nextLine(), "путь к зашифрованному файлу");

        System.out.print("Путь к выходному файлу: ");
        String dest = inputValidator.requirePath(scanner.nextLine(), "путь к выходному файлу");

        System.out.print("Путь к репрезентативному файлу: ");
        String sample = inputValidator.requirePath(scanner.nextLine(), "путь к репрезентативному файлу");

        cipherController.statisticalDecrypt(src, dest, sample);
    }
}
