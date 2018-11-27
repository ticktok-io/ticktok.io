package io.ticktok.server.tick.rabbit;

public class QueueNameCreator {
    private final String name;
    private final String schedule;

    public QueueNameCreator(String name, String schedule) {
        this.name= name;
        this.schedule = schedule;
    }

    public String create() {
        return name + ";" + schedule;
    }
}
