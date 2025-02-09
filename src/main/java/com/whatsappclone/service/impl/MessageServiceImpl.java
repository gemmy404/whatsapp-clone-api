package com.whatsappclone.service.impl;

import com.whatsappclone.dto.MessageRequest;
import com.whatsappclone.dto.MessageResponse;
import com.whatsappclone.entity.ChatEntity;
import com.whatsappclone.entity.MessageEntity;
import com.whatsappclone.entity.Notification;
import com.whatsappclone.enums.MessageState;
import com.whatsappclone.enums.MessageType;
import com.whatsappclone.enums.NotificationType;
import com.whatsappclone.mapper.MessageMapper;
import com.whatsappclone.repository.ChatRepository;
import com.whatsappclone.repository.MessageRepository;
import com.whatsappclone.service.FileService;
import com.whatsappclone.service.MessageService;
import com.whatsappclone.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.whatsappclone.repository.specification.MessageSpecification.findAllMessagesByChatId;
import static com.whatsappclone.service.FileService.readFileFromLocation;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final MessageMapper messageMapper;
    private final FileService fileService;
    private final NotificationService notificationService;

    @Override
    public void saveMessage(MessageRequest request) {
        ChatEntity chat = chatRepository.findById(request.chatId())
                .orElseThrow(() -> new EntityNotFoundException("Chat with id: " + request.chatId() + " not found"));

        MessageEntity message = new MessageEntity();
        message.setChat(chat);
        message.setContent(request.content());
        message.setSenderId(request.senderId());
        message.setReceiverId(request.receiverId());
        message.setType(request.type());
        message.setState(MessageState.SENT);
        messageRepository.save(message);

        Notification notification = Notification.builder()
                .chatId(request.chatId())
                .chatName(chat.getChatName(request.senderId()))
                .senderId(request.senderId())
                .receiverId(request.receiverId())
                .messageType(request.type())
                .content(request.content())
                .type(NotificationType.MESSAGE)
                .build();
        notificationService.sendNotification(request.receiverId(), notification);
    }

    @Override
    public List<MessageResponse> findChatMessages(String chatId) {
        return messageRepository.findAll(findAllMessagesByChatId(chatId))
                .stream()
                .map(messageMapper::toMessageResponse)
                .toList();
    }

    @Override
    @Transactional
    public void setMessagesToSeen(String chatId, Authentication currentUser) {
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat with id: " + chatId + " not found"));

        final String senderId = getSenderId(chat, currentUser);
        final String recipientId = getRecipientId(chat, currentUser);

        messageRepository.setMessagesToSeenByChatId(chatId, MessageState.SEEN);

        Notification notification = Notification.builder()
                .chatId(chatId)
                .senderId(senderId)
                .receiverId(recipientId)
                .type(NotificationType.SEEN)
                .build();
        notificationService.sendNotification(recipientId, notification);

    }

    @Override
    public void uploadMediaMessage(String chatId, MultipartFile file, Authentication currentUser) {
        ChatEntity chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat with id: " + chatId + " not found"));

        final String senderId = getSenderId(chat, currentUser); // real sender of the msg, not sender that initiated chat at 1st time
        final String recipientId = getRecipientId(chat, currentUser); // real receiver of the msg, not receiver in chat obj

        final String filePath = fileService.saveFile(file, senderId);

        MessageEntity message = new MessageEntity();
        message.setChat(chat);
        message.setSenderId(senderId);
        message.setReceiverId(recipientId);
        message.setState(MessageState.SENT);
        message.setType(MessageType.IMAGE);
        message.setMediaFilePath(filePath);
        messageRepository.save(message);

        Notification notification = Notification.builder()
                .chatId(chatId)
                .type(NotificationType.IMAGE)
                .messageType(MessageType.IMAGE)
                .senderId(senderId)
                .receiverId(recipientId)
                .media(readFileFromLocation(filePath))
                .build();
        notificationService.sendNotification(recipientId, notification);
    }


    // ex1:               |         ex2:
    // curr = user1       |         curr = user2
    // sender = user1     |         sender = user1
    // recipient = user2  |         recipient = user2

    private String getSenderId(ChatEntity chat, Authentication currentUser) { // ex1: user1 |  ex2:user2
        if (chat.getSender().getId().equals(currentUser.getName())) {
            return chat.getSender().getId();
        }
        return chat.getRecipient().getId();
    }

    private String getRecipientId(ChatEntity chat, Authentication currentUser) { // ex1: user2 | ex2:user1
        if (chat.getSender().getId().equals(currentUser.getName())) {
            return chat.getRecipient().getId();
        }
        return chat.getSender().getId();
    }

}
