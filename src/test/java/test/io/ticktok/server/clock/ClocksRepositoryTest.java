package test.io.ticktok.server.clock;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import test.io.ticktok.server.tick.SpringMongoConfiguration;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringMongoConfiguration.class})
class ClocksRepositoryTest {

}