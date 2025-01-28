package com.whatsappclone.mapper;

import com.whatsappclone.dto.ChatResponse;
import com.whatsappclone.entity.ChatEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ChatMapper {

    default ChatResponse toChatResponse(ChatEntity chatEntity, String currentUserId) {
        return new ChatResponse(
                chatEntity.getId(),
                chatEntity.getChatName(currentUserId),
                chatEntity.getUnreadMessages(currentUserId),
                chatEntity.getLastMessage(),
                chatEntity.getLastMessageTime(),
                chatEntity.getRecipient().isUserOnline(),
                chatEntity.getSender().getId(),
                chatEntity.getRecipient().getId()
        );
    }

}
