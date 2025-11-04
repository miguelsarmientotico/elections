package pe.elections.microservices.core.candidate;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

public abstract class MongoDbTestBase {

    @Container
    @ServiceConnection
    private static MongoDBContainer database = new MongoDBContainer("mongo:8.2.1");
}
