package test.io.ticktok.server.support;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class RepositoryCleanupConfiguration {

    @Bean
    public RepositoryCleanupExtension repositoryCleanupExtension(MongoTemplate mongo) {
        return new RepositoryCleanupExtension(mongo);
    }


}
