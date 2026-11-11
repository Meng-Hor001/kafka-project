package kh.edu.platform.restapi.dto;

public record MessageResponse(
        String status,
        String topic,
        int partition,
        long offset
) {
}
