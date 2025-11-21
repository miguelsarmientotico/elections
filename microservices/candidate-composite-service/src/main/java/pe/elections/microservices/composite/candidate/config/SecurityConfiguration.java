package pe.elections.microservices.composite.candidate.config;

import static org.springframework.http.HttpMethod.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfiguration {

    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .authorizeExchange(exchange -> exchange
                .pathMatchers("/openapi/**").permitAll()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers(POST, "/candidate-composite/**").hasAuthority("SCOPE_candidate:write")
                .pathMatchers(DELETE, "/candidate-composite/**").hasAuthority("SCOPE_candidate:write")
                .pathMatchers(GET, "/candidate-composite/**").hasAuthority("SCOPE_candidate:read")
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(server -> server.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
