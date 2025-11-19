package pe.elections.springcloud.gateway.config;

import static java.util.logging.Level.FINE;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.CompositeReactiveHealthContributor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthContributor;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Configuration
@DependsOn("loadBalancedWebClientBuilder")
public class HealthCheckConfiguration {
    public static final Logger LOG = LoggerFactory.getLogger(HealthCheckConfiguration.class);

    public final WebClient webClient;

    @Autowired
    public HealthCheckConfiguration(
        WebClient.Builder webClientBuilder
    ) {
        this.webClient = webClientBuilder.build();
    }

    @Bean
    ReactiveHealthContributor healthCheckMicroservices() {
        final Map<String, ReactiveHealthIndicator> registry = new LinkedHashMap<>();
        registry.put("comment", () -> getHealth("http://comment"));
        registry.put("candidate", () -> getHealth("http://candidate"));
        registry.put("newsarticle", () -> getHealth("http://newsarticle"));
        registry.put("candidate-composite", () -> getHealth("http://candidate-composite"));
        registry.put("authserver", () -> getHealth("http://authserver"));
        return CompositeReactiveHealthContributor.fromMap(registry);
    }

    private Mono<Health> getHealth(String baseUrl) {
        String url = baseUrl + "/actuator/health";
        LOG.debug("Configuracion de Health API en el URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
        .map(s -> new Health.Builder().up().build())
        .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
        .log(LOG.getName(), FINE);
    }
}
