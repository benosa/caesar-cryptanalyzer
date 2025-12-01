package com.javarush.infrastructure.configuration;

import com.javarush.application.controllers.CipherController;
import com.javarush.application.handlers.CliApplication;
import com.javarush.domain.FileService;
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
        // Репозиторий работы с файлами (инфраструктура)
        FileSystemRepository fileSystemRepository = new FileSystemRepository();

        // Доменный сервис
        FileService fileService = new FileService(fileSystemRepository);

        // Контроллер
        CipherController cipherController = new CipherController(fileService);

        // CLI-приложение (root-команда Picocli)
        CliApplication cliApplication = new CliApplication(cipherController);

        // Регистрация бинов
        beans.put(FileSystemRepository.class, fileSystemRepository);
        beans.put(FileService.class, fileService);
        beans.put(CipherController.class, cipherController);
        beans.put(CliApplication.class, cliApplication);
    }
}
