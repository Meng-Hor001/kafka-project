package kh.edu.platform.restapi.controller;

import jakarta.validation.Valid;
import kh.edu.platform.application.service.MessageService;
import kh.edu.platform.restapi.dto.MessageRequest;
import kh.edu.platform.restapi.dto.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public CompletableFuture<MessageResponse> publish(
            @Valid @RequestBody MessageRequest request
    ) {
        return messageService.publish(request.key(), request.message())
                .thenApply(result -> new MessageResponse(
                        "SUCCESS",
                        result.topic(),
                        result.partition(),
                        result.offset()
                ));
    }
}
