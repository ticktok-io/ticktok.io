package test.io.ticktok.server.tick.http;

import io.ticktok.server.tick.http.MongoHttpQueuesRepository.OnPopUpdate;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.Map;

import static io.ticktok.server.tick.http.MongoHttpQueuesRepository.LAST_ACCESSED_TIME;
import static org.assertj.core.api.Assertions.assertThat;

class OnPopUpdateTest {

    private static final Date NOW = new Date();

    @Test
    void updateLastAccessedTimeToNow() {
        Update update = new FixedTimeOnPopUpdate().create();
        assertThat(((Map)update.getUpdateObject().get("$set")).get(LAST_ACCESSED_TIME)).isEqualTo(NOW);
    }

    class FixedTimeOnPopUpdate extends OnPopUpdate {

        protected Date now() {
            return NOW;
        }
    }
}