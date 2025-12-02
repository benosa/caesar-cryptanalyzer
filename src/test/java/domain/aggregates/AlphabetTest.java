package domain.aggregates;

import com.javarush.domain.aggregates.Alphabet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlphabetTest {

    @Test
    void sizeShouldBePositive() {
        Alphabet alphabet = new Alphabet();
        assertTrue(alphabet.size() > 0, "Алфавит должен содержать хотя бы один символ");
    }

    @Test
    void containsKnownCharacters() {
        Alphabet alphabet = new Alphabet();

        // предполагаем, что русский алфавит + пробел и знаки
        assertTrue(alphabet.contains('а'), "Алфавит должен содержать букву 'а'");
        assertTrue(alphabet.contains(' '), "Алфавит должен содержать пробел");
    }

    @Test
    void indexOfReturnsNonNegativeForContainedChars() {
        Alphabet alphabet = new Alphabet();

        assertTrue(alphabet.indexOf('а') >= 0, "indexOf для 'а' должен быть >= 0");
    }

    @Test
    void shiftAndUnshiftAreInverseOperations() {
        Alphabet alphabet = new Alphabet();
        char original = 'д';
        int key = 5;

        char shifted = alphabet.shift(original, key);
        char back = alphabet.unshift(shifted, key);

        assertEquals(original, back, "unshift(shift(c, k), k) должен возвращать исходный символ");
    }

    @Test
    void shiftWithZeroKeyReturnsSameCharacter() {
        Alphabet alphabet = new Alphabet();
        char original = 'ж';

        char shifted = alphabet.shift(original, 0);

        assertEquals(original, shifted, "При сдвиге на 0 символ не должен меняться");
    }

    @Test
    void shiftWithFullAlphabetSizeBehavesLikeZero() {
        Alphabet alphabet = new Alphabet();
        char original = 'з';

        char shifted = alphabet.shift(original, alphabet.size());

        assertEquals(original, shifted, "Сдвиг на размер алфавита должен эквивалентен 0");
    }

    @Test
    void containsShouldReturnFalseForNonAlphabetCharacter() {
        Alphabet alphabet = new Alphabet();

        assertFalse(alphabet.contains('#'), "Символ '#' не должен входить в криптоалфавит");
    }
}
