package test.io.ticktok.server.support;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.mongodb.core.MongoTemplate;

public class RepositoryCleanupExtension implements BeforeEachCallback, AfterEachCallback {

    private final MongoTemplate mongo;

    public RepositoryCleanupExtension(MongoTemplate mongo) {
        this.mongo = mongo;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        dropAllCollections();
    }

    private void dropAllCollections() {
        for (String name : mongo.getDb().listCollectionNames()) {
            mongo.getCollection(name).drop();
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        dropAllCollections();
    }

}
