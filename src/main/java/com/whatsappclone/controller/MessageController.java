package com.whatsappclone.controller;

import com.whatsappclone.dto.MessageContentRequest;
import com.whatsappclone.dto.MessageRequest;
import com.whatsappclone.dto.MessageResponse;
import com.whatsappclone.enums.MessageDeleteType;
import com.whatsappclone.service.MessageService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
@Tag(name = "Message")
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<?> saveMessage(@Valid @RequestBody MessageRequest request) {
        messageService.saveMessage(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/upload-media", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadMedia(@RequestParam("chat-id") String chatId,
                                         @Parameter()
                                         @RequestPart("file") MultipartFile file,
                                         Authentication currentUser) throws IOException {
        messageService.uploadMediaMessage(chatId, file, currentUser);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/chat/{chat-id}")
    public ResponseEntity<List<MessageResponse>> getAllMessages(@PathVariable("chat-id") String chatId,
                                                                Authentication currentUser) {
        return ResponseEntity.ok(messageService.findChatMessages(chatId, currentUser));
    }

    @PatchMapping
    public ResponseEntity<?> setMessageToSeen(@RequestParam("chat-id") String chatId, Authentication currentUser) {
        messageService.setMessagesToSeen(chatId, currentUser);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PatchMapping("/contents")
    public ResponseEntity<?> updateMessageContent(@RequestBody MessageContentRequest request,
                                                  Authentication currentUser) {
        messageService.updateMessageContent(request, currentUser);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{message-id}")
    public ResponseEntity<?> deleteMessage(@PathVariable("message-id") Long messageId,
                                           @RequestParam("delete-type") MessageDeleteType deleteType,
                                           Authentication currentUser) {
        messageService.deleteMessage(messageId, deleteType, currentUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
