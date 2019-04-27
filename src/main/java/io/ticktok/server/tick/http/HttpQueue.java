package io.ticktok.server.tick.http;

import io.ticktok.server.tick.TickMessage;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@ToString
@Document
public class HttpQueue {

    @Id
    private String id;
    @Indexed(unique = true)
    private String externalId;
    @Indexed(unique = true)
    private String name;
    private String schedule;
    private List<TickMessage> ticks = new ArrayList<>();
    private Date lastAccessedTime;

}
