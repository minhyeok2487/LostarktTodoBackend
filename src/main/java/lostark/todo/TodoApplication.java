package lostark.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class TodoApplication {

	public static void main(String[] args) {
		System.out.println("TEST");
		SpringApplication.run(TodoApplication.class, args);
	}

}
