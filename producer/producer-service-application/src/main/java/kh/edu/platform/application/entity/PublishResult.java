package kh.edu.platform.application.entity;

public record PublishResult(
        String topic,
        int partition,
        long offset
) {
}
