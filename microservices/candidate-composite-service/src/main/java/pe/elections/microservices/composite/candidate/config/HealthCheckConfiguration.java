package pe.elections.microservices.composite.candidate.config;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import pe.elections.microservices.composite.candidate.services.CandidateCompositeIntegration;

@Configuration
public class HealthCheckConfiguration {

    @Autowired
    CandidateCompositeIntegration integration;

    @Bean
    ReactiveHealthContributor coreServies() {
        final Map<String, ReactiveHealthIndicator> registry = new LinkedHashMap<>();
        //registry.put("candidate", () -> integration.getCandidateHealth());
        //registry.put("comment", () -> integration.getCommentHealth());
        //registry.put("news-article", () -> integration.getNewsArticleHealth());
        return CompositeReactiveHealthContributor.fromMap(registry);
    }
}
