package kh.edu.platform.application.service;

import kh.edu.platform.application.entity.PublishResult;
import kh.edu.platform.application.port.MessagePublisher;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class MessageService {
    private final MessagePublisher messagePublisher;

    public CompletableFuture<PublishResult> publish(
            String key,
            String message
    ) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Key must not be blank");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message must not be blank");
        }

        return messagePublisher.publish(key, message);
    }
}
