package com.javarush.infrastructure.configuration;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AppConfig {

    private final String alphabet;
    private final Charset charset;
    private final int bufferSize;

    private final int spaceWeight;
    private final int vowelWeight;
    private final int punctuationWeight;
    private final String frequentLetters;

    private final int statisticalMinTextLength;

    private AppConfig(
            String alphabet,
            Charset charset,
            int bufferSize,
            int spaceWeight,
            int vowelWeight,
            int punctuationWeight,
            String frequentLetters,
            int statisticalMinTextLength
    ) {
        this.alphabet = alphabet;
        this.charset = charset;
        this.bufferSize = bufferSize;
        this.spaceWeight = spaceWeight;
        this.vowelWeight = vowelWeight;
        this.punctuationWeight = punctuationWeight;
        this.frequentLetters = frequentLetters;
        this.statisticalMinTextLength = statisticalMinTextLength;
    }

    public static AppConfig load() {
        try (InputStream is = AppConfig.class
                .getClassLoader()
                .getResourceAsStream("application.yml")) {

            if (is == null) {
                return defaultConfig();
            }

            Yaml yaml = new Yaml();
            @SuppressWarnings("unchecked")
            Map<String, Object> root = yaml.load(is);

            @SuppressWarnings("unchecked")
            Map<String, Object> crypt = (Map<String, Object>) root.get("cryptanalyzer");
            if (crypt == null) {
                return defaultConfig();
            }

            // alphabet.value
            String alphabet = getString(crypt, "alphabet", "value",
                    "абвгдеёжзийклмнопрстуфхцчшщъыьэюя.,«»\"':-!? ");

            // io.charset, io.bufferSize
            @SuppressWarnings("unchecked")
            Map<String, Object> io = (Map<String, Object>) crypt.get("io");
            String charsetName = io != null
                    ? (String) io.getOrDefault("charset", "UTF-8")
                    : "UTF-8";
            int bufferSize = io != null
                    ? toInt(io.get("bufferSize"), 8192)
                    : 8192;

            // bruteForce.*
            @SuppressWarnings("unchecked")
            Map<String, Object> brute = (Map<String, Object>) crypt.get("bruteForce");
            int spaceWeight = brute != null ? toInt(brute.get("spaceWeight"), 3) : 3;
            int vowelWeight = brute != null ? toInt(brute.get("vowelWeight"), 1) : 1;
            int punctuationWeight = brute != null ? toInt(brute.get("punctuationWeight"), 2) : 2;
            String frequentLetters = brute != null
                    ? (String) brute.getOrDefault("frequentLetters", "оеаин")
                    : "оеаин";

            // statistical.minTextLength
            @SuppressWarnings("unchecked")
            Map<String, Object> statistical = (Map<String, Object>) crypt.get("statistical");
            int minLen = statistical != null
                    ? toInt(statistical.get("minTextLength"), 500)
                    : 500;

            return new AppConfig(
                    alphabet,
                    Charset.forName(charsetName),
                    bufferSize,
                    spaceWeight,
                    vowelWeight,
                    punctuationWeight,
                    frequentLetters,
                    minLen
            );
        } catch (Exception e) {
            // при любой ошибке — дефолтная конфигурация
            return defaultConfig();
        }
    }

    private static AppConfig defaultConfig() {
        return new AppConfig(
                "абвгдеёжзийклмнопрстуфхцчшщъыьэюя.,«»\"':-!? ",
                StandardCharsets.UTF_8,
                8192,
                3,
                1,
                2,
                "оеаин",
                500
        );
    }

    @SuppressWarnings("unchecked")
    private static String getString(Map<String, Object> parent, String key, String subKey, String fallback) {
        Object o = parent.get(key);
        if (!(o instanceof Map)) {
            return fallback;
        }
        Map<String, Object> sub = (Map<String, Object>) o;
        Object val = sub.get(subKey);
        return val instanceof String ? (String) val : fallback;
    }

    private static int toInt(Object value, int fallback) {
        if (value instanceof Number n) {
            return n.intValue();
        }
        if (value instanceof String s) {
            try {
                return Integer.parseInt(s.trim());
            } catch (NumberFormatException ignored) {
            }
        }
        return fallback;
    }

    public String getAlphabet() {
        return alphabet;
    }

    public Charset getCharset() {
        return charset;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public int getSpaceWeight() {
        return spaceWeight;
    }

    public int getVowelWeight() {
        return vowelWeight;
    }

    public int getPunctuationWeight() {
        return punctuationWeight;
    }

    public String getFrequentLetters() {
        return frequentLetters;
    }

    public int getStatisticalMinTextLength() {
        return statisticalMinTextLength;
    }
}
