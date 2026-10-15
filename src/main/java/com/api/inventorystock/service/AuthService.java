package com.api.inventorystock.service;

import com.api.inventorystock.common.response.TokenResponse;
import com.api.inventorystock.model.request.*;
import com.api.inventorystock.model.response.AppUserDto;
import com.api.inventorystock.model.response.MailResponse;
import jakarta.validation.Valid;

public interface AuthService {
    AppUserDto create(AppUserAddRequest request);
    TokenResponse login(AuthRequest request);
    TokenResponse refresh(RefreshTokenRequest request);
    MailResponse sendOtp(MailRequest.SendOtp request);
    void verifyOtp(MailRequest.VerifyOtp request);
    void resetPassword(ResetPasswordRequest request);
}
