package com.javarush.infrastructure.configuration;

import com.javarush.application.controllers.CipherController;
import com.javarush.application.handlers.CliApplication;
import com.javarush.domain.FileService;
import com.javarush.domain.aggregates.Alphabet;
import com.javarush.domain.ports.in.CipherService;
import com.javarush.domain.ports.out.TextRepository;
import com.javarush.infrastructure.repository.FileSystemRepository;

import java.util.HashMap;
import java.util.Map;

public class IoCContainer {

    private static IoCContainer INSTANCE;

    private final Map<Class<?>, Object> beans = new HashMap<>();

    private IoCContainer() {
        registerBeans();
    }

    public static IoCContainer init() {
        if (INSTANCE == null) {
            INSTANCE = new IoCContainer();
        }
        return INSTANCE;
    }

    public static IoCContainer getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("IoCContainer not initialized. Call IoCContainer.init() first.");
        }
        return INSTANCE;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> type) {
        Object bean = beans.get(type);
        if (bean == null) {
            throw new IllegalStateException("No bean registered for type: " + type.getName());
        }
        return (T) bean;
    }

    private void registerBeans() {
        // 1. конфигурация
        AppConfig config = AppConfig.load();

        // 2. алфавит (доменная сущность)
        Alphabet alphabet = new Alphabet(config.getAlphabet());

        // 3. репозиторий (инфраструктура)
        TextRepository fileSystemRepository = new FileSystemRepository(
                config.getCharset(),
                config.getBufferSize()
        );

        // 4. доменный сервис, работающий через порты
        CipherService fileService = new FileService(
                fileSystemRepository,
                alphabet,
                config.getSpaceWeight(),
                config.getVowelWeight(),
                config.getPunctuationWeight(),
                config.getFrequentLetters(),
                config.getStatisticalMinTextLength()
        );

        // 5. контроллер (application layer)
        CipherController cipherController = new CipherController(fileService);

        // 6. CLI-приложение (handler / точка входа Picocli)
        CliApplication cliApplication = new CliApplication(cipherController);

        // Регистрация бинов
        beans.put(AppConfig.class, config);
        beans.put(Alphabet.class, alphabet);
        beans.put(TextRepository.class, fileSystemRepository);
        beans.put(CipherService.class, fileService);
        beans.put(FileService.class, fileService);      // если где-то просят конкретный класс
        beans.put(CipherController.class, cipherController);
        beans.put(CliApplication.class, cliApplication);
    }
}
