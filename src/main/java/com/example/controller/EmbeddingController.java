package com.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.ai.exception.EmbeddingCustomException;
import org.springframework.ai.service.EmbeddingService;
import org.springframework.ai.utility.CustomDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.ai.utility.AiConstants.GENERATE_AN_EMBEDDING_VECTOR_FOR_THE_GIVEN_TEXT;

public class EmbeddingController {

    private final EmbeddingService embeddingService;

    public EmbeddingController(EmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }

    @PostMapping
    @Operation(summary = GENERATE_AN_EMBEDDING_VECTOR_FOR_THE_GIVEN_TEXT)
    public ResponseEntity<CustomDto.EmbedResponse> embed(@Valid @RequestBody CustomDto.EmbedRequest embedRequest, WebRequest request) {
        try {
            return ResponseEntity.ok(embeddingService.embed(embedRequest));
        } catch (Exception e) {
            throw new EmbeddingCustomException(e.getMessage(), request);
        }
    }
}