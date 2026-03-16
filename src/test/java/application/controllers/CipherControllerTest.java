package application.controllers;

import com.javarush.application.controllers.CipherController;
import com.javarush.domain.ports.in.CipherService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CipherControllerTest {

    @Mock
    CipherService cipherService;

    @Test
    void encryptDelegatesToCipherService() {
        CipherController controller = new CipherController(cipherService);

        controller.encrypt("src.txt", "dest.txt", 3);

        verify(cipherService).encryptFile("src.txt", "dest.txt", 3);
    }

    @Test
    void decryptDelegatesToCipherService() {
        CipherController controller = new CipherController(cipherService);

        controller.decrypt("enc.txt", "out.txt", 5);

        verify(cipherService).decryptFile("enc.txt", "out.txt", 5);
    }

    @Test
    void bruteForceDelegatesToCipherService() {
        CipherController controller = new CipherController(cipherService);

        controller.bruteForce("enc.txt", "out.txt", "sample.txt");

        verify(cipherService).bruteForceDecryptFile("enc.txt", "out.txt", "sample.txt");
    }

    @Test
    void statisticalDecryptDelegatesToCipherService() {
        CipherController controller = new CipherController(cipherService);

        controller.statisticalDecrypt("enc.txt", "out.txt", "sample.txt");

        verify(cipherService).statisticalDecryptFile("enc.txt", "out.txt", "sample.txt");
    }
}
