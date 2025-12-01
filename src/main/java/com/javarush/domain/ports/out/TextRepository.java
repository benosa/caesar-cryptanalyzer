package com.javarush.domain.ports.out;

public interface TextRepository {

    String readText(String path);

    void writeText(String path, String content);

    boolean exists(String path);
}
