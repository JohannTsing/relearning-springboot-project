package com.johann.config;

/**
 * <p>
 *
 * @author Johann
 * @version 1.0
 * @see
 **/
import org.springframework.boot.Banner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

public class MyBanner implements Banner {

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream printStream) {
        printStream.printf("\nSpring Boot Version: %s", SpringBootVersion.getVersion());
        printStream.printf("\nWelcome to %s!", environment.getProperty("spring.application.name"));
    }

}
