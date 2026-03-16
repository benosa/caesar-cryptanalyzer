package com.javarush.domain;

import com.javarush.application.errors.FileProcessingException;
import com.javarush.application.errors.InvalidInputException;
import com.javarush.domain.aggregates.Alphabet;
import com.javarush.domain.ports.in.CipherService;
import com.javarush.domain.ports.out.TextRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.function.BiConsumer;

public class FileService implements CipherService {

    private final TextRepository textRepository;
    private final Alphabet alphabet;

    private final int spaceWeight;
    private final int vowelWeight;
    private final int punctuationWeight;
    private final String frequentLetters;
    private final int statisticalMinTextLength;

    // Частотная модель русского текста для брутфорса без семпла
    private final double[] russianExpectedFreq;

    public FileService(TextRepository textRepository) {
        this(
                textRepository,
                new Alphabet(),
                3,
                1,
                2,
                "оеаин",
                500
        );
    }

    public FileService(
            TextRepository textRepository,
            Alphabet alphabet,
            int spaceWeight,
            int vowelWeight,
            int punctuationWeight,
            String frequentLetters,
            int statisticalMinTextLength
    ) {
        this.textRepository = textRepository;
        this.alphabet = alphabet;
        this.spaceWeight = spaceWeight;
        this.vowelWeight = vowelWeight;
        this.punctuationWeight = punctuationWeight;
        this.frequentLetters = frequentLetters != null ? frequentLetters : "оеаин";
        this.statisticalMinTextLength = statisticalMinTextLength;
        this.russianExpectedFreq = buildRussianExpectedFreq();
    }

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

    /**
     * Brute-force:
     * - если есть samplePath — используем частотный анализ по sample
     * - если samplePath нет — бьёмся об встроенную модель частот русского текста
     *
     * @return найденный ключ
     */
    public int bruteForceDecryptFile(String sourcePath, String destPath, String samplePath) {
        validatePaths(sourcePath, destPath);

        String encrypted = textRepository.readAll(sourcePath);

        // === Есть sample: частотный анализ по репрезентативному тексту ===
        if (samplePath != null && !samplePath.isBlank()) {
            if (!textRepository.exists(samplePath)) {
                throw new InvalidInputException("Репрезентативный файл не существует: " + samplePath);
            }
            String sample = textRepository.readAll(samplePath);

            double[] encryptedFreq = buildFrequencies(encrypted);
            double[] sampleFreq = buildFrequencies(sample);

            int bestKey = findBestKeyByFreq(encryptedFreq, sampleFreq);
            String decrypted = decryptText(encrypted, bestKey);
            textRepository.writeAll(destPath, decrypted);
            return bestKey;
        }

        // === Нет sample: частотный анализ против встроенной русской модели ===
        double[] encryptedFreq = buildFrequencies(encrypted);
        int bestKey = findBestKeyByFreq(encryptedFreq, russianExpectedFreq);
        String bestText = decryptText(encrypted, bestKey);

        textRepository.writeAll(destPath, bestText);
        return bestKey;
    }

    public void statisticalDecryptFile(String sourcePath, String destPath, String samplePath) {
        validatePaths(sourcePath, destPath);

        if (samplePath == null || samplePath.isBlank()) {
            throw new InvalidInputException("Для статистического анализа нужно указать путь к репрезентативному файлу.");
        }
        if (!textRepository.exists(samplePath)) {
            throw new InvalidInputException("Репрезентативный файл не существует: " + samplePath);
        }

        String encrypted = textRepository.readAll(sourcePath);
        if (encrypted.length() < statisticalMinTextLength) {
            throw new InvalidInputException(
                    "Текст слишком короткий для статистического анализа (минимум "
                            + statisticalMinTextLength + " символов)."
            );
        }

        String sample = textRepository.readAll(samplePath);

        double[] encryptedFreq = buildFrequencies(encrypted);
        double[] sampleFreq = buildFrequencies(sample);

        int bestKey = findBestKeyByFreq(encryptedFreq, sampleFreq);

        String decrypted = decryptText(encrypted, bestKey);
        textRepository.writeAll(destPath, decrypted);
    }

    // ======= ВНУТРЕННЯЯ ЛОГИКА ШИФРОВАНИЯ =======

    private char encryptChar(char ch, int key) {
        char lower = Character.toLowerCase(ch);
        if (!alphabet.contains(lower)) {
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

    // Используем encryptChar/decryptChar, чтобы сохранять регистр
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

    // ======= ВАЛИДАЦИЯ =======

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

    // ======= ЭВРИСТИКИ / СТАТИСТИКА =======

    private double scoreSimple(String text) {
        int score = 0;
        for (char ch : text.toCharArray()) {
            char c = Character.toLowerCase(ch);
            if (c == ' ') {
                score += spaceWeight;
            }
            if (frequentLetters.indexOf(c) >= 0) {
                score += vowelWeight;
            }
            if (c == '.' || c == ',' || c == '!' || c == '?' || c == ':' || c == '«' || c == '»' || c == '-') {
                score += punctuationWeight;
            }
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

    private int findBestKeyByFreq(double[] encryptedFreq, double[] sampleFreq) {
        int bestKey = 0;
        double bestScore = Double.POSITIVE_INFINITY;

        for (int key = 0; key < alphabet.size(); key++) {
            double diff = diffForShift(encryptedFreq, sampleFreq, key);
            if (diff < bestScore) {
                bestScore = diff;
                bestKey = key;
            }
        }

        return bestKey;
    }

    private double[] buildRussianExpectedFreq() {
        double[] freq = new double[alphabet.size()];

        BiConsumer<Character, Double> put = (ch, value) -> {
            int idx = alphabet.indexOf(ch);
            if (idx >= 0) {
                freq[idx] = value;
            }
        };

        // Примерные частоты для русского языка + пробел
        put.accept(' ', 0.17);

        put.accept('о', 0.11);
        put.accept('е', 0.085);
        put.accept('а', 0.08);
        put.accept('и', 0.073);
        put.accept('н', 0.067);
        put.accept('т', 0.063);
        put.accept('с', 0.055);
        put.accept('р', 0.047);
        put.accept('в', 0.045);
        put.accept('л', 0.044);
        put.accept('к', 0.035);
        put.accept('м', 0.032);
        put.accept('д', 0.03);
        put.accept('п', 0.028);
        put.accept('у', 0.026);
        put.accept('я', 0.021);
        put.accept('ы', 0.019);
        put.accept('з', 0.016);
        put.accept('ь', 0.015);
        put.accept('б', 0.014);
        put.accept('г', 0.013);
        put.accept('ч', 0.012);
        put.accept('й', 0.01);
        put.accept('х', 0.009);
        put.accept('ж', 0.009);
        put.accept('ш', 0.007);
        put.accept('ю', 0.006);
        put.accept('ц', 0.005);
        put.accept('щ', 0.004);
        put.accept('э', 0.003);
        put.accept('ф', 0.002);
        put.accept('ъ', 0.0004);
        put.accept('ё', 0.0004);

        // Немного пунктуации
        put.accept('.', 0.01);
        put.accept(',', 0.02);

        // Нормализуем
        double sum = 0.0;
        for (double v : freq) {
            sum += v;
        }
        if (sum > 0) {
            for (int i = 0; i < freq.length; i++) {
                freq[i] /= sum;
            }
        }

        return freq;
    }
}
