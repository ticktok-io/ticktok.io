package io.ticktok.server;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SpringBootApplication
public class Application {

    private static ConfigurableApplicationContext context;

    public static void main(String... args) {
        context = SpringApplication.run(Application.class, args);
    }

    public static void restart(String brokerMode) {
        ApplicationArguments args = context.getBean(ApplicationArguments.class);

        Thread thread = new Thread(() -> {
            context.close();
            context = SpringApplication.run(Application.class, join(brokerOptionFor(brokerMode), args.getSourceArgs()));
        });
        thread.setDaemon(false);
        thread.start();
    }

    private static String[] join(String[]... args) {
        return Stream.of(args).flatMap(Stream::of).toArray(String[]::new);
    }

    private static String[] brokerOptionFor(String mode) {
        return new String[]{"--spring.profiles.active=" + mode};
    }
}
