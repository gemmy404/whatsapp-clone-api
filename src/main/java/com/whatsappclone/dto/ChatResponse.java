package com.whatsappclone.dto;

import java.time.LocalDateTime;

public record ChatResponse(
        String id,
        String name,
        long unreadCount,
        String lastMessage,
        LocalDateTime lastMessageTime,
        boolean isRecipientOnline,
        String senderId,
        String receiverId
) {
}
