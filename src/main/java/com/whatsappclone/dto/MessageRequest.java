package com.whatsappclone.dto;

import com.whatsappclone.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record MessageRequest(
        @NotBlank
        @NotEmpty
        String content,
        @NotNull
        String senderId,
        @NotNull
        String receiverId,
        @NotNull
        MessageType type,
        @NotNull
        @NotEmpty
        String chatId
) {
}
