package kh.edu.istad.platform.consumerserviceinfrastructure.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "concurrentKafkaListenerContainerFactory"
    )
    public void consume(String message) {

        log.info("Message received: {}", message);

    }
}