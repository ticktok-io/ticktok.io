package io.ticktok.server.tick.httplong;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

@RestController
@Profile("http-long")
public class ChannelsController {


    private final TicksPoller tickPoller;

    public ChannelsController(ChannelsRepository channelsRepository) {
        this.tickPoller = new TicksPoller();
    }

    @PostMapping(LongPollConfiguration.POLL_PATH)
    public DeferredResult<ResponseEntity<?>> poll(@RequestBody PollRequest pollRequest) {
        DeferredResult<ResponseEntity<?>> output = new DeferredResult<>(5L * 1000);
        ForkJoinPool.commonPool().submit(() -> {
            output.setResult(ResponseEntity.ok(tickPoller.poll(pollRequest.getChannels())));
        });
        output.onTimeout(() -> {
           output.setResult(ResponseEntity.ok(new ArrayList<>()));
        });
        return output;
    }
}
