package com.whatsappclone.service.impl;

import com.whatsappclone.dto.MessageContentRequest;
import com.whatsappclone.dto.MessageRequest;
import com.whatsappclone.dto.MessageResponse;
import com.whatsappclone.entity.ChatEntity;
import com.whatsappclone.entity.MessageEntity;
import com.whatsappclone.entity.Notification;
import com.whatsappclone.enums.MessageDeleteType;
import com.whatsappclone.enums.MessageState;
import com.whatsappclone.enums.MessageType;
import com.whatsappclone.enums.NotificationType;
import com.whatsappclone.exception.OperationNotPermittedException;
import com.whatsappclone.mapper.MessageMapper;
import com.whatsappclone.repository.ChatRepository;
import com.whatsappclone.repository.MessageRepository;
import com.whatsappclone.repository.UserRepository;
import com.whatsappclone.service.FileService;
import com.whatsappclone.service.MessageService;
import com.whatsappclone.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static com.whatsappclone.repository.specification.MessageSpecification.findAllMessagesByChatId;
import static com.whatsappclone.service.FileService.readFileFromLocation;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;
    private final FileService fileService;
    private final NotificationService notificationService;

    @Override
    public void saveMessage(MessageRequest request) {
        ChatEntity chat = chatRepository.findById(request.chatId())
                .orElseThrow(() -> new EntityNotFoundException("Chat with id: " + request.chatId() + " not found"));

        userRepository.findById(request.senderId()).orElseThrow(() -> new
                UsernameNotFoundException("Sender with id: " + request.senderId() + " not found"));
        userRepository.findById(request.receiverId()).orElseThrow(() -> new
                UsernameNotFoundException("Receiver with id: " + request.receiverId() + " not found"));

        MessageEntity message = new MessageEntity();
        message.setChat(chat);
        message.setContent(request.content());
        message.setSenderId(request.senderId());
        message.setReceiverId(request.receiverId());
        message.setType(request.type());
        message.setState(MessageState.SENT);
        messageRepository.save(message);

        chat.setLastModifiedDate(LocalDateTime.now());
        chatRepository.save(chat);

        Notification notification = Notification.builder()
                .chatId(request.chatId())
                .chatName(chat.getTargetChatName(request.senderId()))
                .senderId(request.senderId())
                .receiverId(request.receiverId())
                .messageType(request.type())
                .content(request.content())
                .type(NotificationType.MESSAGE)
                .build();
        notificationService.sendNotification(request.receiverId(), notification);
    }

    @Override
    public List<MessageResponse> findChatMessages(String chatId, Authentication currentUser) {
        final String userId = currentUser.getName();
        chatRepository.findById(chatId)
                .orElseThrow(() -> new EntityNotFoundException("Chat with id: " + chatId + " not found"));
        return messageRepository.findAll(findAllMessagesByChatId(chatId, userId))
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

        chat.setLastModifiedDate(LocalDateTime.now());
        chatRepository.save(chat);

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

    @Override
    public void updateMessageContent(MessageContentRequest request, Authentication currentUser) {
        final String userId = currentUser.getName();
        MessageEntity savedMessage = messageRepository.findById(request.messageId()).orElseThrow(() -> new
                EntityNotFoundException("Message with id: " + request.messageId() + " not found"));
        if (!savedMessage.getSenderId().equals(userId)) {
            throw new OperationNotPermittedException("You do not have permission to edit this message");
        }
        savedMessage.setContent(request.newContent());
        messageRepository.save(savedMessage);
    }

    @Override
    public void deleteMessage(Long messageId, @NotEmpty MessageDeleteType deleteType, Authentication currentUser) {
        final String userId = currentUser.getName();
        MessageEntity savedMessage = messageRepository.findById(messageId).orElseThrow(() -> new
                EntityNotFoundException("Message with id: " + messageId + " not found"));

        if (savedMessage.getSenderId().equals(userId) && deleteType.equals(MessageDeleteType.DELETE_FOR_ME)) {
            savedMessage.setSenderDeleted(true);
            messageRepository.save(savedMessage);
        } else if (savedMessage.getReceiverId().equals(userId) && deleteType.equals(MessageDeleteType.DELETE_FOR_ME)) {
            savedMessage.setReceiverDeleted(true);
            messageRepository.save(savedMessage);
        } else if (savedMessage.getSenderId().equals(userId) && deleteType.equals(MessageDeleteType.DELETE_FOR_EVERYONE)) {
            messageRepository.deleteById(messageId);
        } else {
            throw new OperationNotPermittedException("You do not have permission to delete this message");
        }
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
