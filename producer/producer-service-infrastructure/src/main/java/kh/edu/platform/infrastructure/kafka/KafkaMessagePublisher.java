package kh.edu.platform.infrastructure.kafka;

import kh.edu.platform.application.entity.PublishResult;
import kh.edu.platform.application.port.MessagePublisher;
import kh.edu.platform.infrastructure.mapper.PublishMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class KafkaMessagePublisher implements MessagePublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private final PublishMapper mapper;

    private static final Logger log =
            LoggerFactory.getLogger(KafkaMessagePublisher.class);



    public KafkaMessagePublisher(
            PublishMapper mapper, KafkaTemplate<String, String> kafkaTemplate,
            @Value("${app.kafka.topic}") String topic
    ) {
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Override
    public CompletableFuture<PublishResult> publish(
            String key,
            String message
    ) {
        try {
            return kafkaTemplate.send(topic, key, message)
                    .thenApply(mapper::toPublishResult)
                    .whenComplete((result, exception) -> {
                        if (exception != null) {
                            log.error(
                                    "Failed to publish message to topic={}",
                                    topic,
                                    exception
                            );
                        } else {
                            log.info(
                                    "Message published: topic={}, partition={}, offset={}",
                                    result.topic(),
                                    result.partition(),
                                    result.offset()
                            );
                        }
                    });
        } catch (RuntimeException exception) {
            log.error(
                    "Failed to initiate publishing to topic={}",
                    topic,
                    exception
            );

            return CompletableFuture.failedFuture(exception);
        }
    }
}
