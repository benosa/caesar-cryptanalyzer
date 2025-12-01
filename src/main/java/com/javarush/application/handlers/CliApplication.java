package com.javarush.application.handlers;

import com.javarush.application.controllers.CipherController;
import com.javarush.application.errors.CryptanalyzerException;
import com.javarush.application.errors.FileProcessingException;
import com.javarush.application.errors.InvalidInputException;
import picocli.CommandLine.Command;

import java.util.Scanner;

@Command(
        name = "cypher",
        description = "Caesar cipher CLI application",
        mixinStandardHelpOptions = true
)
public class CliApplication implements Runnable {

    private final CipherController cipherController;

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

    // ========= обёртки с обработкой ошибок =========

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
        } catch (NumberFormatException e) {
            System.err.println("[INPUT ERROR] Ключ должен быть целым числом.");
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
        } catch (NumberFormatException e) {
            System.err.println("[INPUT ERROR] Ключ должен быть целым числом.");
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

    // ========= "чистые" хэндлеры без try/catch =========

    private void handleEncrypt(Scanner scanner) {
        System.out.print("Путь к исходному файлу: ");
        String src = scanner.nextLine().trim();

        System.out.print("Путь к выходному файлу: ");
        String dest = scanner.nextLine().trim();

        System.out.print("Ключ (целое число): ");
        int key = Integer.parseInt(scanner.nextLine().trim());

        cipherController.encrypt(src, dest, key);
    }

    private void handleDecrypt(Scanner scanner) {
        System.out.print("Путь к зашифрованному файлу: ");
        String src = scanner.nextLine().trim();

        System.out.print("Путь к выходному файлу: ");
        String dest = scanner.nextLine().trim();

        System.out.print("Ключ (целое число): ");
        int key = Integer.parseInt(scanner.nextLine().trim());

        cipherController.decrypt(src, dest, key);
    }

    private void handleBruteForce(Scanner scanner) {
        System.out.print("Путь к зашифрованному файлу: ");
        String src = scanner.nextLine().trim();

        System.out.print("Путь к выходному файлу: ");
        String dest = scanner.nextLine().trim();

        System.out.print("Путь к репрезентативному файлу (Enter, если нет): ");
        String sample = scanner.nextLine().trim();
        if (sample.isBlank()) {
            sample = null;
        }

        cipherController.bruteForce(src, dest, sample);
    }

    private void handleStatistical(Scanner scanner) {
        System.out.print("Путь к зашифрованному файлу: ");
        String src = scanner.nextLine().trim();

        System.out.print("Путь к выходному файлу: ");
        String dest = scanner.nextLine().trim();

        System.out.print("Путь к репрезентативному файлу: ");
        String sample = scanner.nextLine().trim();

        cipherController.statisticalDecrypt(src, dest, sample);
    }
}
