package pe.elections.microservices.core.candidate.services;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pe.elections.microservices.api.core.candidate.Candidate;
import pe.elections.microservices.api.core.candidate.CandidateService;
import pe.elections.microservices.api.event.Event;
import pe.elections.microservices.api.exceptions.EventProcessingException;

@Configuration
public class MessageProcessorConfig {
    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final CandidateService candidateService;

    @Autowired
    public MessageProcessorConfig(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @Bean
    public Consumer<Event<Integer, Candidate>> messageProcessor() {
        return event -> {
            switch (event.getEventType()) {
                case CREATE:
                    Candidate candidate = event.getData();
                    candidateService.createCandidate(candidate).block();
                    break;
                case DELETE:
                    int candidateId = event.getKey();
                    candidateService.deleteCandidate(candidateId).block();
                    break;
                default:
                    String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
                    LOG.warn(errorMessage);
                    throw new EventProcessingException(errorMessage);
            }
        };
    }
}
