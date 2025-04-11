package com.whatsappclone.service.impl;

import com.whatsappclone.dto.ChatResponse;
import com.whatsappclone.dto.StringResponse;
import com.whatsappclone.entity.ChatEntity;
import com.whatsappclone.entity.UserEntity;
import com.whatsappclone.mapper.ChatMapper;
import com.whatsappclone.repository.ChatRepository;
import com.whatsappclone.repository.UserRepository;
import com.whatsappclone.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.whatsappclone.repository.specification.ChatSpecification.*;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ChatResponse> getChatsByReceiverId(Authentication currentUser) {
        final String userId = currentUser.getName();
        return chatRepository.findAll(findAllChatsByUserId(userId))
                .stream()
                .map(chat -> chatMapper.toChatResponse(chat, userId))
                .toList();
    }

    @Override
    public List<ChatResponse> getUnreadChatsByReceiverId(Authentication currentUser) {
        final String userId = currentUser.getName();
        return chatRepository.findAll(findUnreadChatsByUserId(userId))
                .stream()
                .map(chat -> chatMapper.toChatResponse(chat, userId))
                .toList();
    }

    @Override
    public StringResponse createChat(String senderId, String receiverId) {
        Optional<ChatEntity> existingChat = chatRepository
                .findOne(findChatBySenderIdAndRecipientId(senderId, receiverId));
        if (existingChat.isPresent()) {
            return new StringResponse(existingChat.get().getId());
        }

        UserEntity sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UsernameNotFoundException("User with id: " + senderId + " not found"));
        UserEntity receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UsernameNotFoundException("User with id: " + receiverId + " not found"));

        ChatEntity chat = new ChatEntity();
        chat.setSender(sender);
        chat.setRecipient(receiver);

        ChatEntity savedChat = chatRepository.save(chat);
        return new StringResponse(savedChat.getId());
    }

    @Override
    public List<ChatResponse> searchChatsByName(String name, Authentication currentUser) {
        final String userId = currentUser.getName();
        return chatRepository.findAll(findChatsByName(userId, name))
                .stream()
                .map(chat -> chatMapper.toChatResponse(chat, userId))
                .toList();
    }

}
