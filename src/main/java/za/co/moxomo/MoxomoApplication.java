package co.moxomo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoxomoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoxomoApplication.class, args);
	}
}
