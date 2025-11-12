package pe.elections.microservices.core.newsarticle;

import java.util.function.Consumer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class DiagnosticTest extends MySqlTestBase{

    @Autowired
    private ApplicationContext context;

    @Test
    void diagnoseBeans() {
        System.out.println("=== BEAN DIAGNOSIS ===");
        
        // Verificar si el bean messageProcessor existe
        String[] consumerBeans = context.getBeanNamesForType(Consumer.class);
        System.out.println("Consumer beans found: " + consumerBeans.length);
        for (String beanName : consumerBeans) {
            System.out.println(" - " + beanName);
        }
        
        // Verificar específicamente messageProcessor
        if (context.containsBean("messageProcessor")) {
            System.out.println("✓ messageProcessor bean EXISTS");
        } else {
            System.out.println("✗ messageProcessor bean NOT FOUND");
        }
        
        // Verificar NewsArticleService
        if (context.containsBean("newsArticleService")) {
            System.out.println("✓ newsArticleService bean EXISTS");
        } else {
            System.out.println("✗ newsArticleService bean NOT FOUND");
        }
    }
}
