package test.io.ticktok.server.tick.repository;

import io.ticktok.server.tick.repository.TicksPurger;
import io.ticktok.server.tick.repository.TicksRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TicksPurgerTest.TicksPurgerTestConfiguration.class})
class TicksPurgerTest {

    @Configuration
    @ComponentScan(basePackages = "io.ticktok.server.tick.repository")
    static class TicksPurgerTestConfiguration {
        @Bean
        public TicksRepository ticksRepository() {
            return mock(TicksRepository.class);
        }
    }

    @Autowired
    TicksRepository repository;
    @Autowired
    TicksPurger purger;


    @Test
    void purgeTicks() {
        purger.purge();
        verify(repository).deletePublishedExceptLastPerSchedule(10);
    }
}