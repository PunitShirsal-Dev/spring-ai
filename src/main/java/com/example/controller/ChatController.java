package com.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.ai.entity.ChatUser;
import org.springframework.ai.exception.ChatCustomException;
import org.springframework.ai.service.ChatService;
import org.springframework.ai.utility.Ai;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.ai.utility.Constants.*;

public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    @Operation(summary = BLOCKING_CHAT_SEND_A_MESSAGE_AND_GET_A_FULL_RESPONSE)
    public ResponseEntity<Ai.ChatResponse> chat(
            @Valid @RequestBody Ai.ChatRequest chatRequest,
            @AuthenticationPrincipal ChatUser user,
            WebRequest request) {
        try {
            return ResponseEntity.ok(chatService.chat(chatRequest, user));
        } catch (Exception e) {
            throw new ChatCustomException(e.getMessage(), request);
        }
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = STREAMING_CHAT_RESPONSE_DELIVERED_AS_SERVER_SENT_EVENTS)
    public Flux<String> streamChat(
            @Valid @RequestBody Ai.ChatRequest chatRequest,
            @AuthenticationPrincipal ChatUser user,
            WebRequest request) {
        try {
            return chatService.streamChat(chatRequest, user);
        } catch (Exception e) {
            throw new ChatCustomException(e.getMessage(), request);
        }
    }

    @GetMapping("/history/{sessionId}")
    @Operation(summary = GET_CONVERSATION_HISTORY_FOR_A_SESSION)
    public ResponseEntity<List<Ai.HistoryEntry>> history(
            @PathVariable String sessionId,
            @AuthenticationPrincipal ChatUser user,
            WebRequest request) {
        try {
            return ResponseEntity.ok(chatService.getHistory(sessionId, user));
        } catch (Exception e) {
            throw new ChatCustomException(e.getMessage(), request);
        }
    }

    @DeleteMapping("/history/{sessionId}")
    @Operation(summary = CLEAR_A_CONVERSATION_SESSION)
    public ResponseEntity<Void> clearHistory(@PathVariable String sessionId, WebRequest request) {
        try {
            chatService.clearHistory(sessionId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ChatCustomException(e.getMessage(), request);
        }
    }
}