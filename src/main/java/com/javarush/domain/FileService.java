package com.javarush.domain;

import com.javarush.domain.aggregates.Alphabet;
import com.javarush.domain.ports.out.TextRepository;

public class FileService {

    private final TextRepository textRepository;
    private final Alphabet alphabet = new Alphabet();

    public FileService(TextRepository textRepository) {
        this.textRepository = textRepository;
    }

    public void encryptFile(String sourcePath, String destPath, int key) {
        validatePaths(sourcePath, destPath);
        validateKey(key);

        String text = textRepository.readText(sourcePath);
        String encrypted = encryptText(text, key);
        textRepository.writeText(destPath, encrypted);
    }

    public void decryptFile(String sourcePath, String destPath, int key) {
        validatePaths(sourcePath, destPath);
        validateKey(key);

        String text = textRepository.readText(sourcePath);
        String decrypted = decryptText(text, key);
        textRepository.writeText(destPath, decrypted);
    }

    public void bruteForceDecryptFile(String sourcePath, String destPath, String samplePath) {
        validatePaths(sourcePath, destPath);

        String encrypted = textRepository.readText(sourcePath);
        String sample = null;

        if (samplePath != null && !samplePath.isBlank()) {
            if (!textRepository.exists(samplePath)) {
                throw new InvalidInputException("Репрезентативный файл не существует: " + samplePath);
            }
            sample = textRepository.readText(samplePath);
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

        textRepository.writeText(destPath, bestText);
    }

    public void statisticalDecryptFile(String sourcePath, String destPath, String samplePath) {
        validatePaths(sourcePath, destPath);

        if (samplePath == null || samplePath.isBlank()) {
            throw new InvalidInputException("Для статистического анализа нужно указать путь к репрезентативному файлу.");
        }
        if (!textRepository.exists(samplePath)) {
            throw new InvalidInputException("Репрезентативный файл не существует: " + samplePath);
        }

        String encrypted = textRepository.readText(sourcePath);
        String sample = textRepository.readText(samplePath);

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
        textRepository.writeText(destPath, decrypted);
    }


    private String encryptText(String text, int key) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char ch : text.toCharArray()) {
            // приведение к нижнему, если работаешь в нижнем регистре
            char lower = Character.toLowerCase(ch);
            char shifted = alphabet.shift(lower, key);
            sb.append(shifted);
        }
        return sb.toString();
    }

    private String decryptText(String text, int key) {
        StringBuilder sb = new StringBuilder(text.length());
        for (char ch : text.toCharArray()) {
            char lower = Character.toLowerCase(ch);
            char shifted = alphabet.unshift(lower, key);
            sb.append(shifted);
        }
        return sb.toString();
    }

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

    // Простейшая эвристика: считаем пробелы и "типичные" буквы
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

    // Если есть репрезентативный текст — сравниваем частоты символов
    private double scoreBySample(String candidate, String sample) {
        double[] freqCandidate = buildFrequencies(candidate);
        double[] freqSample = buildFrequencies(sample);

        // используем минус сумму квадратов отклонения (чем ближе, тем больше score)
        double sse = 0.0;
        for (int i = 0; i < freqCandidate.length; i++) {
            double d = freqCandidate[i] - freqSample[i];
            sse += d * d;
        }
        return -sse;
    }

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
