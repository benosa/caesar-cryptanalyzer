package com.javarush.application.handlers;

import com.javarush.application.controllers.CipherController;
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
                case "1" -> handleEncrypt(scanner);
                case "2" -> handleDecrypt(scanner);
                case "3" -> handleBruteForce(scanner);
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
        // System.out.println("4. Статистический анализ");
        System.out.println("0. Выход");
        System.out.print("Выбор: ");
    }

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
}
