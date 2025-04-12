package com.whatsappclone.dto;

import com.whatsappclone.enums.MessageState;
import com.whatsappclone.enums.MessageType;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        String content,
        MessageType type,
        MessageState state,
        String senderId,
        String receiverId,
        LocalDateTime createdAt,
        byte[] media,
        Boolean senderDeleted,
        Boolean receiverDeleted
) {
}
