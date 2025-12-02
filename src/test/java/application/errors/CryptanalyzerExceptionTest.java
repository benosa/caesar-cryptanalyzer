package application.errors;

import com.javarush.application.errors.CryptanalyzerException;
import com.javarush.application.errors.FileProcessingException;
import com.javarush.application.errors.InvalidInputException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CryptanalyzerExceptionTest {

    @Test
    void cryptanalyzerExceptionStoresMessageAndCause() {
        Throwable cause = new RuntimeException("cause");
        CryptanalyzerException ex = new CryptanalyzerException("msg", cause);

        assertEquals("msg", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }

    @Test
    void invalidInputExceptionIsCryptanalyzerException() {
        InvalidInputException ex = new InvalidInputException("bad input");

        assertTrue(ex instanceof CryptanalyzerException);
        assertEquals("bad input", ex.getMessage());
    }

    @Test
    void fileProcessingExceptionIsCryptanalyzerException() {
        Throwable cause = new RuntimeException("io");
        FileProcessingException ex = new FileProcessingException("file error", cause);

        assertTrue(ex instanceof CryptanalyzerException);
        assertEquals("file error", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
