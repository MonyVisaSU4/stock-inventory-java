package com.api.inventorystock.controller;

import com.api.inventorystock.common.response.SuccessResponse;
import com.api.inventorystock.common.response.TokenResponse;
import com.api.inventorystock.model.request.*;
import com.api.inventorystock.model.response.AppUserDto;
import com.api.inventorystock.model.response.MailResponse;
import com.api.inventorystock.service.AuthService;
import com.api.inventorystock.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<SuccessResponse<AppUserDto>> create(@Valid @RequestBody AppUserAddRequest request) {
        return ResponseEntity.ok(
                ApiResponseUtil.success(HttpStatus.CREATED, authService.create(request))
        );
    }

    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<TokenResponse>> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(
                ApiResponseUtil.success(HttpStatus.OK, authService.login(request))
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<SuccessResponse<TokenResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(
                ApiResponseUtil.success(HttpStatus.OK, authService.refresh(request))
        );
    }

    @PostMapping("/send-otp")
    public ResponseEntity<SuccessResponse<MailResponse>> sendOtp(@Valid @RequestBody MailRequest.SendOtp request){
        return ResponseEntity.ok(
                ApiResponseUtil.success(
                        HttpStatus.OK,
                        authService.sendOtp(request)
                )
        );
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<SuccessResponse<Void>> verifyOtp(@Valid @RequestBody MailRequest.VerifyOtp request){
        authService.verifyOtp(request);
        return ResponseEntity.ok(
                ApiResponseUtil.success(HttpStatus.OK)
        );
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<SuccessResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request){
        authService.resetPassword(request);
        return ResponseEntity.ok(
                ApiResponseUtil.success(HttpStatus.OK)
        );
    }
}
