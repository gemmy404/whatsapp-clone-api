package com.whatsappclone.service;

import com.whatsappclone.dto.MessageContentRequest;
import com.whatsappclone.dto.MessageRequest;
import com.whatsappclone.dto.MessageResponse;
import com.whatsappclone.enums.MessageDeleteType;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessageService {

    void saveMessage(MessageRequest request);

    List<MessageResponse> findChatMessages(String chatId, Authentication currentUser);

    void setMessagesToSeen(String chatId, Authentication currentUser);

    void uploadMediaMessage(String chatId, MultipartFile file, Authentication currentUser);

    void updateMessageContent(MessageContentRequest request, Authentication currentUser);

    void deleteMessage(Long messageId, MessageDeleteType deleteType, Authentication currentUser);

}
