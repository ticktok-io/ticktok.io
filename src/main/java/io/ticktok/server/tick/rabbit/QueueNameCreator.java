package io.ticktok.server.tick.rabbit;

import io.ticktok.server.clock.Clock;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class QueueNameCreator {
    private final String name;
    private final String schedule;

    public QueueNameCreator(String name, String schedule) {
        this.name= name;
        this.schedule = schedule;
    }

    public String create() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((name + ";" + schedule).getBytes());
            //return new String(md.digest());
            return name + ";" + schedule;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
