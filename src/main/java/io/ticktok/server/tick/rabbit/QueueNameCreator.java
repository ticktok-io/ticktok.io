package io.ticktok.server.tick.rabbit;

import io.ticktok.server.clock.Clock;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class QueueNameCreator {
    private final Clock clock;

    public QueueNameCreator(Clock clock) {
        this.clock = clock;
    }

    public String create() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update((clock.getName() + ";" + clock.getSchedules()).getBytes());
            return new String(md.digest());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
