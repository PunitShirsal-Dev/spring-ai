package com.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.ai.exception.RagCustomException;
import org.springframework.ai.service.RagService;
import org.springframework.ai.utility.CustomDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.ai.utility.AiConstants.*;

public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping(value = "/ingest/pdf", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize(HAS_ROLE_ADMIN)
    @Operation(summary = UPLOAD_A_PDF_FOR_INGESTION_INTO_THE_VECTOR_STORE_ADMIN_ONLY)
    public ResponseEntity<IngestResponse> ingestPdf(
            @RequestParam(FILE) MultipartFile file,
            WebRequest request) throws IOException {
        try {
            if (file.isEmpty()) return ResponseEntity.badRequest().build();
            return ResponseEntity.ok(ragService.ingestPdf(file));
        } catch (Exception e) {
            throw new RagCustomException(e.getMessage(), request);
        }
    }

    @PostMapping("/ingest/text")
    @PreAuthorize(HAS_ROLE_ADMIN)
    @Operation(summary = INGEST_RAW_TEXT_INTO_THE_VECTOR_STORE_ADMIN_ONLY)
    public ResponseEntity<IngestResponse> ingestText(
            @RequestParam String text,
            @RequestParam(defaultValue = MANUAL) String source,
            WebRequest request) throws IOException {
        try {
            return ResponseEntity.ok(ragService.ingestText(text, source));
        } catch (Exception e) {
            throw new RagCustomException(e.getMessage(), request);
        }
    }

    @PostMapping("/query")
    @Operation(summary = ASK_A_QUESTION_AGAINST_THE_KNOWLEDGE_BASE)
    public ResponseEntity<RagResponse> query(
            @Valid @RequestBody RagRequest ragRequest, WebRequest request) {
        try {
            return ResponseEntity.ok(ragService.query(ragRequest));
        } catch (Exception e) {
            throw new RagCustomException(e.getMessage(), request);
        }
    }
}
