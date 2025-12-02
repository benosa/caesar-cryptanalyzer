package com.javarush;

import com.javarush.application.handlers.CliApplication;
import com.javarush.infrastructure.configuration.IoCContainer;
import picocli.CommandLine;

public class Main {

    public static void main(String[] args) {
        IoCContainer.init();
        CliApplication app = IoCContainer.getInstance().get(CliApplication.class);
        new CommandLine(app).execute(args);
    }
}
