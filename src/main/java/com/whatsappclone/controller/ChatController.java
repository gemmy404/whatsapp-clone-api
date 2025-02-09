package com.whatsappclone.controller;

import com.whatsappclone.dto.ChatResponse;
import com.whatsappclone.dto.StringResponse;
import com.whatsappclone.service.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name = "Chat")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<StringResponse> createChat(@RequestParam(name = "sender-id") String senderId,
                                                     @RequestParam(name = "receiver-id") String receiverId) {
        return new ResponseEntity<>(chatService.createChat(senderId, receiverId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ChatResponse>> getChatsByReceiver(Authentication currentUser) {
        return ResponseEntity.ok(chatService.getChatsByReceiverId(currentUser));
    }

}
