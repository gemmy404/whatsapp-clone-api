package com.whatsappclone.dto;

import com.whatsappclone.enums.MessageType;

public record MessageRequest(
        String content,
        String senderId,
        String receiverId,
        MessageType type,
        String chatId
) {
}
