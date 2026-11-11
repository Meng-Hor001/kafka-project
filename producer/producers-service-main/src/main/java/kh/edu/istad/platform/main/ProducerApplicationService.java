package kh.edu.istad.platform.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "kh.edu.istad.platform")
public class ProducerApplicationService {
    public static void main(String[] args){
            SpringApplication.run(ProducerApplicationService.class, args);
    }
}

