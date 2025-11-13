package pe.elections.microservices.core.comment.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.index.IndexResolver;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.index.ReactiveIndexOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.stereotype.Component;

import pe.elections.microservices.core.comment.persistence.CommentEntity;

import org.springframework.context.event.ContextRefreshedEvent;

@Component
public class MongoDBIndexConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MongoDBIndexConfig.class);

    @Autowired
    ReactiveMongoOperations mongoTemplate;

    @EventListener(ContextRefreshedEvent.class)
    public void initIndicesAfterStartup() {
        LOG.info("Initializing MongoDB indexes for CommentEntity...");
        MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext = mongoTemplate.getConverter().getMappingContext();
        IndexResolver resolver = new MongoPersistentEntityIndexResolver(mappingContext);
        ReactiveIndexOperations indexOps = mongoTemplate.indexOps(CommentEntity.class);
        resolver.resolveIndexFor(CommentEntity.class).forEach(e -> indexOps.createIndex(e).block());
        LOG.info("MongoDB indexes initialized successfully");
    }
}
