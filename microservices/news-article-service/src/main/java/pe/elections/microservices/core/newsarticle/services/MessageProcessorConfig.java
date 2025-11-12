package pe.elections.microservices.core.newsarticle.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pe.elections.microservices.api.core.newsarticle.NewsArticle;
import pe.elections.microservices.api.core.newsarticle.NewsArticleService;
import pe.elections.microservices.api.event.Event;
import pe.elections.microservices.api.exceptions.EventProcessingException;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class MessageProcessorConfig {
    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);
    private final NewsArticleService newsArticleService;

    @Autowired
    public MessageProcessorConfig(NewsArticleService newsArticleService) {
        this.newsArticleService = newsArticleService;
    }

    @Bean
    public Consumer<Event<Integer, NewsArticle>> messageProcessor() {
        LOG.info("=== MESSAGE PROCESSOR BEAN CREATED ===");
        LOG.info("NewsArticleService: " + (newsArticleService != null ? "FOUND" : "NULL"));  // ← Agregar esto
        return event -> {
            switch (event.getEventType()) {
                case CREATE:
                    LOG.info("Create Event");
                    NewsArticle newsArticle = event.getData();
                    newsArticleService.createNewsArticle(newsArticle).block();
                    break;
                case DELETE:
                    LOG.info("delete Event");
                    int candidateId = event.getKey();
                    newsArticleService.deleteNewsArticle(candidateId).block();
                    break;

                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                    throw new EventProcessingException(errorMessage);
            }
            LOG.info("Message processing done!");

        };
    }

}
