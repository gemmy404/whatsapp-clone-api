package com.whatsappclone.service;

import com.whatsappclone.entity.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String userId, Notification notification) { // userId is whom that want to send notification
        log.info("Sending WS notification to {} with payload {}", userId, notification);
        // Send to userId (whom want to send), then destination, and obj (payload)
        messagingTemplate.convertAndSendToUser(userId, "/chat", notification);
    }
}
