package com.javarush;

import com.javarush.application.handlers.CliApplication;
import com.javarush.infrastructure.configuration.IoCContainer;
import picocli.CommandLine;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        IoCContainer container = IoCContainer.init();

        // Достаём из контейнера корневое CLI-приложение
        CliApplication cliApplication = container.get(CliApplication.class);

        // Picocli запускает наш хэндлер
        int exitCode = new CommandLine(cliApplication).execute(args);
        System.exit(exitCode);
    }
}