package kh.edu.istad.platform.consumerservicemain;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "kh.edu.istad.platform")
public class ConsumerApplicationService {

	public static void main(String[] args) {
		SpringApplication.run(
				ConsumerApplicationService.class,
				args
		);
	}
}