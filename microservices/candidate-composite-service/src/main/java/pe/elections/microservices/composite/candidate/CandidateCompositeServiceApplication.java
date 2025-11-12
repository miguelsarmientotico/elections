package pe.elections.microservices.composite.candidate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("pe.elections.microservices")
public class CandidateCompositeServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CandidateCompositeServiceApplication.class, args);
	}

}
