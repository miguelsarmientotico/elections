package pe.elections.microservices.core.newsarticle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("pe.elections.microservices")
public class NewsArticleServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NewsArticleServiceApplication.class, args);
	}

}
