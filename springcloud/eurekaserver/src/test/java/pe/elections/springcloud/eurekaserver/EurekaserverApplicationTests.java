package pe.elections.springcloud.eurekaserver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class EurekaserverApplicationTests {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void contextLoads() {
        // Solo esto es suficiente
    }

    @Test
    void healthEndpointReturnsUp() {
        ResponseEntity<String> entity = testRestTemplate.getForEntity("/actuator/health", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue(entity.getBody().contains("\"status\":\"UP\"")); // ← JUnit
    }

    @Test
    void eurekaEndpointReturnsOk() {
        ResponseEntity<String> entity = testRestTemplate.getForEntity("/eureka/apps", String.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertTrue(entity.getBody().contains("\"applications\"")); // ← JUnit
    }

}
