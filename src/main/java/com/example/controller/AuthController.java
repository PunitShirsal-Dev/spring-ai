package com.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.ai.exception.AuthCustomException;
import org.springframework.ai.service.AuthService;
import org.springframework.ai.utility.CustomDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

import static org.springframework.ai.utility.AiConstants.*;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = REGISTER_A_NEW_USER, responses = {
            @ApiResponse(responseCode = CREATED_STATUS_CODE, description = USER_REGISTERED_SUCCESSFULLY),
            @ApiResponse(responseCode = BAD_REQUEST_STATUS_CODE, description = USERNAME_EMAIL_ALREADY_TAKEN_OR_VALIDATION_ERROR)})
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody RegisterRequest registerRequest,
            WebRequest request) {
        TokenResponse tokenResponse;
        try {
            tokenResponse = authService.register(registerRequest);
        } catch (Exception e) {
            throw new AuthCustomException(e.getMessage(), request);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse(request, tokenResponse, USER_REGISTERED_SUCCESSFULLY1));
    }

    @PostMapping("/login")
    @Operation(summary = LOGIN_WITH_USERNAME_AND_PASSWORD, responses = {
            @ApiResponse(responseCode = OK_STATUS_CODE, description = LOGIN_SUCCESSFUL),
            @ApiResponse(responseCode = UNAUTHORIZED_STATUS_CODE, description = INVALID_CREDENTIALS)})
    public ResponseEntity<ApiResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            WebRequest request) {
        TokenResponse login;
        try {
            login = authService.login(loginRequest);
        } catch (Exception e) {
            throw new AuthCustomException(e.getMessage(), request);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse(request, login, USER_LOGGED_IN_SUCCESSFULLY));

    }

    @PostMapping("/refresh")
    @Operation(summary = REFRESH_ACCESS_TOKEN_USING_REFRESH_TOKEN, responses = {
            @ApiResponse(responseCode = OK_STATUS_CODE, description = TOKEN_REFRESHED),
            @ApiResponse(responseCode = BAD_REQUEST_STATUS_CODE, description = INVALID_OR_EXPIRED_REFRESH_TOKEN)})
    public ResponseEntity<ApiResponse> refresh(
            @Valid @RequestBody RefreshRequest refreshRequest,
            WebRequest request) {
        TokenResponse refresh;
        try {
            refresh = authService.refresh(refreshRequest);
        } catch (Exception e) {
            throw new AuthCustomException(e.getMessage(), request);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse(request, refresh, USER_REFRESH_SUCCESSFULLY));
    }

    @GetMapping("/info")
    @Operation(summary = GET_CURRENTLY_AUTHENTICATED_USER_INFO)
    public ResponseEntity<ApiResponse> me(Authentication auth, WebRequest request) {
        Map<String, String> map;
        try {
            map = Map.of(USERNAME, auth.getName());
        } catch (Exception e) {
            throw new AuthCustomException(e.getMessage(), request);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse(request, map, USER_FETCH_SUCCESSFULLY));
    }
}
