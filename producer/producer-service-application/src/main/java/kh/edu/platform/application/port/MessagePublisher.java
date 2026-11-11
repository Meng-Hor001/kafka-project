package kh.edu.platform.application.port;

import kh.edu.platform.application.entity.PublishResult;

import java.util.concurrent.CompletableFuture;

public interface MessagePublisher {
    CompletableFuture<PublishResult> publish(String key, String message);
}
