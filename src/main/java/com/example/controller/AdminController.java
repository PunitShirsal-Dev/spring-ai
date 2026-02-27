package com.example.controller;


import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.entity.ChatUser;
import org.springframework.ai.exception.AdminCustomException;
import org.springframework.ai.repository.ChatUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;

import static org.springframework.ai.utility.AiConstants.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize(HAS_ROLE_ADMIN)
@Hidden
@Tag(name = ADMIN1, description = USER_MANAGEMENT_ADMIN_ROLE_REQUIRED_FOR_ALL_ENDPOINTS)
@SecurityRequirement(name = BEARER_AUTH)
public class AdminController {

    private final ChatUserRepository userRepository;

    public AdminController(ChatUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/users")
    @Operation(summary = LIST_ALL_REGISTERED_USERS)
    public ResponseEntity<ApiResponse> listUsers(WebRequest request) {
        List<Map<String, Object>> list;
        try {
            list = userRepository.findAll().stream().map(this::userToMap).toList();
        } catch (Exception e) {
            throw new AdminCustomException(e.getMessage(), request);
        }
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse(request, list, FIND_ALL_USERS_SUCCESSFULLY));
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = UPDATE_A_USER_S_ROLE_USER_OR_ADMIN)
    public ResponseEntity<ApiResponse> updateRole(@PathVariable Long id, @RequestParam String role, WebRequest request) {
        Map<String, String> map;
        try {
            ChatUser chatUser = userRepository.findById(id).orElseThrow(() -> new AdminCustomException("User with id: " + id + " not found", request));
            chatUser.setRole(ChatUser.Role.valueOf(role.toUpperCase()));
            userRepository.save(chatUser);
            map = Map.of(MESSAGE, ROLE_UPDATED_TO + role.toUpperCase());
        } catch (Exception e) {
            throw new AdminCustomException(e.getMessage(), request);
        }
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse(request, map, UPDATE_USER_ROLE_SUCCESSFULLY));
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = DELETE_A_USER_BY_ID)
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id, WebRequest request) {
        try {
            userRepository.deleteById(id);
        } catch (Exception e) {
            throw new AdminCustomException(e.getMessage(), request);
        }
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse(request, DELETED, DELETE_USER_SUCCESSFULLY));
    }

    private Map<String, Object> userToMap(ChatUser u) {
        return Map.of(ID, u.getId(), USERNAME, u.getUsername(), EMAIL, u.getEmail(), ROLE, u.getRole().name(), CREATED_AT, u.getCreatedAt().toString());
    }
}

