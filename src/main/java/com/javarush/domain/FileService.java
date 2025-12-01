package com.javarush.domain;

import com.javarush.application.errors.FileProcessingException;
import com.javarush.application.errors.InvalidInputException;
import com.javarush.domain.aggregates.Alphabet;
import com.javarush.domain.ports.out.TextRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class FileService {

    private final TextRepository textRepository;
    private final Alphabet alphabet = new Alphabet();

    public FileService(TextRepository textRepository) {
        this.textRepository = textRepository;
    }

    // ================== БОЛЬШИЕ ФАЙЛЫ: ШИФРОВАНИЕ ==================

    public void encryptFile(String sourcePath, String destPath, int key) {
        validatePaths(sourcePath, destPath);
        validateKey(key);

        try (BufferedReader reader = textRepository.openReader(sourcePath);
             BufferedWriter writer = textRepository.openWriter(destPath)) {

            int ch;
            while ((ch = reader.read()) != -1) {
                char original = (char) ch;
                char processed = encryptChar(original, key);
                writer.write(processed);
            }

        } catch (IOException e) {
            throw new FileProcessingException(
                    "Ошибка при шифровании файла: " + sourcePath + " -> " + destPath, e
            );
        }
    }

    // ================== БОЛЬШИЕ ФАЙЛЫ: ДЕШИФРОВАНИЕ ==================

    public void decryptFile(String sourcePath, String destPath, int key) {
        validatePaths(sourcePath, destPath);
        validateKey(key);

        try (BufferedReader reader = textRepository.openReader(sourcePath);
             BufferedWriter writer = textRepository.openWriter(destPath)) {

            int ch;
            while ((ch = reader.read()) != -1) {
                char original = (char) ch;
                char processed = decryptChar(original, key);
                writer.write(processed);
            }

        } catch (IOException e) {
            throw new FileProcessingException(
                    "Ошибка при расшифровке файла: " + sourcePath + " -> " + destPath, e
            );
        }
    }

    // ================== BRUTE FORCE (ЧИТАЕМ ЦЕЛИКОМ) ==================

    public void bruteForceDecryptFile(String sourcePath, String destPath, String samplePath) {
        validatePaths(sourcePath, destPath);

        String encrypted = textRepository.readAll(sourcePath);
        String sample = null;

        if (samplePath != null && !samplePath.isBlank()) {
            if (!textRepository.exists(samplePath)) {
                throw new InvalidInputException("Репрезентативный файл не существует: " + samplePath);
            }
            sample = textRepository.readAll(samplePath);
        }

        int bestKey = 0;
        double bestScore = Double.NEGATIVE_INFINITY;
        String bestText = encrypted;

        for (int key = 0; key < alphabet.size(); key++) {
            String candidate = decryptText(encrypted, key);
            double score = (sample != null)
                    ? scoreBySample(candidate, sample)
                    : scoreSimple(candidate);

            if (score > bestScore) {
                bestScore = score;
                bestKey = key;
                bestText = candidate;
            }
        }

        textRepository.writeAll(destPath, bestText);
        // bestKey можно логировать/возвращать, если потребуется
    }

    // ================== СТАТИСТИЧЕСКИЙ АНАЛИЗ (ЧИТАЕМ ЦЕЛИКОМ) ==================

    public void statisticalDecryptFile(String sourcePath, String destPath, String samplePath) {
        validatePaths(sourcePath, destPath);

        if (samplePath == null || samplePath.isBlank()) {
            throw new InvalidInputException("Для статистического анализа нужно указать путь к репрезентативному файлу.");
        }
        if (!textRepository.exists(samplePath)) {
            throw new InvalidInputException("Репрезентативный файл не существует: " + samplePath);
        }

        String encrypted = textRepository.readAll(sourcePath);
        String sample = textRepository.readAll(samplePath);

        double[] encryptedFreq = buildFrequencies(encrypted);
        double[] sampleFreq = buildFrequencies(sample);

        int bestKey = 0;
        double bestScore = Double.POSITIVE_INFINITY;

        for (int key = 0; key < alphabet.size(); key++) {
            double diff = diffForShift(encryptedFreq, sampleFreq, key);
            if (diff < bestScore) {
                bestScore = diff;
                bestKey = key;
            }
        }

        String decrypted = decryptText(encrypted, bestKey);
        textRepository.writeAll(destPath, decrypted);
    }

    // ================== РАБОТА С ОДНИМ СИМВОЛОМ ==================

    private char encryptChar(char ch, int key) {
        char lower = Character.toLowerCase(ch);
        if (!alphabet.contains(lower)) {
            // символ не из алфавита — не трогаем
            return ch;
        }
        char shifted = alphabet.shift(lower, key);
        if (Character.isUpperCase(ch)) {
            return Character.toUpperCase(shifted);
        }
        return shifted;
    }

    private char decryptChar(char ch, int key) {
        char lower = Character.toLowerCase(ch);
        if (!alphabet.contains(lower)) {
            return ch;
        }
        char shifted = alphabet.unshift(lower, key);
        if (Character.isUpperCase(ch)) {
            return Character.toUpperCase(shifted);
        }
        return shifted;
    }

    // ================== РАБОТА СО СТРОКАМИ (ДЛЯ BRUTE/СТАТИСТИКИ) ==================

    private String encryptText(String text, int key) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char ch : text.toCharArray()) {
            sb.append(encryptChar(ch, key));
        }
        return sb.toString();
    }

    private String decryptText(String text, int key) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char ch : text.toCharArray()) {
            sb.append(decryptChar(ch, key));
        }
        return sb.toString();
    }

    // ================== ВАЛИДАЦИЯ ==================

    private void validatePaths(String sourcePath, String destPath) {
        if (sourcePath == null || sourcePath.isBlank()) {
            throw new InvalidInputException("Не указан путь к исходному файлу.");
        }
        if (!textRepository.exists(sourcePath)) {
            throw new InvalidInputException("Исходный файл не существует: " + sourcePath);
        }
        if (destPath == null || destPath.isBlank()) {
            throw new InvalidInputException("Не указан путь к выходному файлу.");
        }
    }

    private void validateKey(int key) {
        if (key < 0) {
            throw new InvalidInputException("Ключ должен быть неотрицательным.");
        }
    }

    // ================== ЭВРИСТИКА ДЛЯ BRUTE FORCE ==================

    private double scoreSimple(String text) {
        int score = 0;
        for (char ch : text.toCharArray()) {
            char c = Character.toLowerCase(ch);
            if (c == ' ') score += 3;
            if (c == 'о' || c == 'е' || c == 'а' || c == 'и' || c == 'н') score += 1;
            if (c == '.' || c == ',' || c == '!' || c == '?') score += 2;
        }
        return score;
    }

    private double scoreBySample(String candidate, String sample) {
        double[] freqCandidate = buildFrequencies(candidate);
        double[] freqSample = buildFrequencies(sample);

        double sse = 0.0;
        for (int i = 0; i < freqCandidate.length; i++) {
            double d = freqCandidate[i] - freqSample[i];
            sse += d * d;
        }
        return -sse; // чем меньше отклонение, тем выше score
    }

    // ================== СТАТИСТИКА ЧАСТОТ ==================

    private double[] buildFrequencies(String text) {
        double[] freq = new double[alphabet.size()];
        int total = 0;
        for (char ch : text.toCharArray()) {
            char c = Character.toLowerCase(ch);
            if (alphabet.contains(c)) {
                int idx = alphabet.indexOf(c);
                if (idx >= 0) {
                    freq[idx]++;
                    total++;
                }
            }
        }
        if (total > 0) {
            for (int i = 0; i < freq.length; i++) {
                freq[i] /= total;
            }
        }
        return freq;
    }

    private double diffForShift(double[] encryptedFreq, double[] sampleFreq, int shift) {
        double sse = 0.0;
        int n = encryptedFreq.length;
        for (int i = 0; i < n; i++) {
            int shiftedIndex = Math.floorMod(i + shift, n);
            double d = encryptedFreq[shiftedIndex] - sampleFreq[i];
            sse += d * d;
        }
        return sse;
    }
}
