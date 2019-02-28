package io.ticktok.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Application {

    private static ConfigurableApplicationContext context;

    public static void main(String... args) {
        context = SpringApplication.run(Application.class, args);
    }

    public static void restart(String... args) {
        System.gc();
        Thread thread = new Thread(() -> {
            context.close();
            System.gc();
            context = SpringApplication.run(Application.class, args);
            context.refresh();
        });
        thread.setDaemon(false);
        thread.start();
    }

}
