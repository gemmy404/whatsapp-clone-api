package com.whatsappclone.controller;

import com.whatsappclone.dto.MessageRequest;
import com.whatsappclone.dto.MessageResponse;
import com.whatsappclone.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<?> saveMessage(@RequestBody MessageRequest request) {
        messageService.saveMessage(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/upload-media", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadMedia(@RequestParam("chat-id") String chatId,
                                         // todo add @Parameter from swagger
                                         @RequestPart("file") MultipartFile file,
                                         Authentication currentUser) throws IOException {
        messageService.uploadMediaMessage(chatId, file, currentUser);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PatchMapping
    public ResponseEntity<?> setMessagesToSeen(@RequestParam("chat-id") String chatId, Authentication currentUser) {
        messageService.setMessagesToSeen(chatId, currentUser);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/chat/{chat-id}")
    public ResponseEntity<List<MessageResponse>> getChatMessages(@PathVariable("chat-id") String chatId) {
        return ResponseEntity.ok(messageService.findChatMessages(chatId));
    }

}
