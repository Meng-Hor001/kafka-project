package kh.edu.istad.platform.main.bean;

import kh.edu.platform.application.port.MessagePublisher;
import kh.edu.platform.application.service.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public MessageService messageService(MessagePublisher messagePublisher){
        return new MessageService(messagePublisher);
    }
}
