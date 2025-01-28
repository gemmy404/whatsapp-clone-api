package com.whatsappclone.service;

import com.whatsappclone.dto.ChatResponse;
import com.whatsappclone.dto.StringResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ChatService {

    List<ChatResponse> getChatsByReceiverId(Authentication currentUser);

    StringResponse createChat(String senderId, String receiverId);

}
