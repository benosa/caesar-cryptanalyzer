package infrastructure.configuration;

import com.javarush.application.controllers.CipherController;
import com.javarush.domain.FileService;
import com.javarush.infrastructure.configuration.IoCContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IoCContainerTest {

    @BeforeAll
    void setUp() {
        // как бы важно))): инициализируем контейнер ОДИН РАЗ перед всеми тестами
        IoCContainer.init();
    }

    @Test
    void getInstanceReturnsSingleton() {
        IoCContainer c1 = IoCContainer.getInstance();
        IoCContainer c2 = IoCContainer.getInstance();

        assertSame(c1, c2, "IoCContainer должен быть синглтоном");
    }

    @Test
    void containerProvidesCipherControllerBean() {
        IoCContainer container = IoCContainer.getInstance();

        CipherController controller = container.get(CipherController.class);

        assertNotNull(controller, "Контейнер должен вернуть бин CipherController");
    }

    @Test
    void containerProvidesCipherServiceBean() {
        IoCContainer container = IoCContainer.getInstance();

        FileService fileService = container.get(FileService.class);

        assertNotNull(fileService, "Контейнер должен вернуть бин FileService");
    }

    @Test
    void getBeanThrowsForUnknownType() {
        IoCContainer container = IoCContainer.getInstance();

        class UnknownType {}

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> container.get(UnknownType.class),
                "Для неизвестного типа контейнер должен кидать исключение"
        );

        assertTrue(
                ex.getMessage() == null
                        || ex.getMessage().toLowerCase().contains("unknown")
                        || ex.getMessage().toLowerCase().contains("not found")
                        || ex.getMessage().toLowerCase().contains("bean"),
                "Сообщение об ошибке должно как-то указывать на отсутствие бина"
        );
    }
}
