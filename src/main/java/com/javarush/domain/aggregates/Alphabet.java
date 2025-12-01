package com.javarush.domain.aggregates;

import java.util.HashMap;
import java.util.Map;

public class Alphabet {

    // Русский алфавит + знаки препинания, как в задании
    private static final char[] ALPHABET = {
            'а','б','в','г','д','е','ж','з',
            'и','к','л','м','н','о','п','р',
            'с','т','у','ф','х','ц','ч','ш',
            'щ','ъ','ы','ь','э','я',
            '.', ',', '«', '»', '"', '\'', ':', '!', '?', ' '
    };

    private static final Map<Character, Integer> INDEX = new HashMap<>();

    static {
        for (int i = 0; i < ALPHABET.length; i++) {
            INDEX.put(ALPHABET[i], i);
        }
    }

    public int size() {
        return ALPHABET.length;
    }

    public boolean contains(char ch) {
        return INDEX.containsKey(ch);
    }

    public char shift(char ch, int key) {
        Integer idx = INDEX.get(ch);
        if (idx == null) {
            // символ не в алфавите — можно возвращать как есть
            return ch;
        }
        int newIndex = Math.floorMod(idx + key, ALPHABET.length);
        return ALPHABET[newIndex];
    }

    public char unshift(char ch, int key) {
        Integer idx = INDEX.get(ch);
        if (idx == null) {
            return ch;
        }
        int newIndex = Math.floorMod(idx - key, ALPHABET.length);
        return ALPHABET[newIndex];
    }
}
