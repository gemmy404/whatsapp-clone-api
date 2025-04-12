package com.whatsappclone.service;

import com.whatsappclone.dto.MessageRequest;
import com.whatsappclone.dto.MessageResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MessageService {

    void saveMessage(MessageRequest request);

    List<MessageResponse> findChatMessages(String chatId, Authentication currentUser);

    void setMessagesToSeen(String chatId, Authentication currentUser);

    void uploadMediaMessage(String chatId, MultipartFile file, Authentication currentUser);

}
