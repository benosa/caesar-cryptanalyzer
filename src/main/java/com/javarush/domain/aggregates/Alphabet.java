package com.javarush.domain.aggregates;

public class Alphabet {

    private final char[] chars;

    // дефолт — на случай прямого использования в тестах/старом коде
    public Alphabet() {
        this("абвгдеёжзийклмнопрстуфхцчшщъыьэюя.,«»\"':-!? ");
    }

    public Alphabet(String alphabet) {
        if (alphabet == null || alphabet.isEmpty()) {
            throw new IllegalArgumentException("Alphabet string must not be null or empty");
        }
        this.chars = alphabet.toCharArray();
    }

    public int size() {
        return chars.length;
    }

    public boolean contains(char c) {
        return indexOf(c) >= 0;
    }

    public int indexOf(char c) {
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == c) {
                return i;
            }
        }
        return -1;
    }

    public char charAt(int index) {
        return chars[index];
    }

    public char shift(char c, int key) {
        int idx = indexOf(c);
        if (idx < 0) {
            return c;
        }
        int newIndex = Math.floorMod(idx + key, chars.length);
        return chars[newIndex];
    }

    public char unshift(char c, int key) {
        int idx = indexOf(c);
        if (idx < 0) {
            return c;
        }
        int newIndex = Math.floorMod(idx - key, chars.length);
        return chars[newIndex];
    }
}
