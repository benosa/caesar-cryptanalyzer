package com.javarush.domain.aggregates;

/**
 * Алфавит для шифра Цезаря.
 * Русские буквы + базовая пунктуация + пробел.
 * Все в нижнем регистре, регистр восстанавливается в сервисе.
 */
public class Alphabet {

    private static final char[] ALPHABET = {
            'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з',
            'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п',
            'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч',
            'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я',
            '.', ',', '«', '»', '"', '\'', ':', '-',
            '!', '?', ' '
    };

    public int size() {
        return ALPHABET.length;
    }

    public boolean contains(char c) {
        return indexOf(c) >= 0;
    }

    public int indexOf(char c) {
        for (int i = 0; i < ALPHABET.length; i++) {
            if (ALPHABET[i] == c) {
                return i;
            }
        }
        return -1;
    }

    public char charAt(int index) {
        return ALPHABET[index];
    }

    public char shift(char c, int key) {
        int idx = indexOf(c);
        if (idx < 0) {
            return c;
        }
        int n = ALPHABET.length;
        int shifted = Math.floorMod(idx + key, n);
        return ALPHABET[shifted];
    }

    public char unshift(char c, int key) {
        int idx = indexOf(c);
        if (idx < 0) {
            return c;
        }
        int n = ALPHABET.length;
        int shifted = Math.floorMod(idx - key, n);
        return ALPHABET[shifted];
    }
}
