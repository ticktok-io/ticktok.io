package io.ticktok.server.tick.httplong;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public interface ChannelsRepository {

    void updateLastPollTime(List<String> ids, long timestamp);

    @ResponseStatus(code = BAD_REQUEST)
    class ChannelNotExistsException extends RuntimeException {
    }
}
