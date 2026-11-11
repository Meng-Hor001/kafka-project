package kh.edu.platform.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public record KafkaTopicProperties(
        String topic,
        int partitions,
        int replicas
) {
    public KafkaTopicProperties {
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("Kafka topic is required");
        }
        if (partitions < 1 || replicas < 1) {
            throw new IllegalArgumentException(
                    "Kafka partitions and replicas must be positive");
        }
    }
}
