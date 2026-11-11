package kh.edu.platform.restapi.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageRequest(

        @NotBlank(message = "Key must not be blank")
        String key,

        @NotBlank(message = "Message must not be blank")
        String message
) {
}
