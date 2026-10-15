package com.api.inventorystock.service.impl;

import com.api.inventorystock.common.constant.MessageConstant;
import com.api.inventorystock.common.response.TokenResponse;
import com.api.inventorystock.exception.CustomException;
import com.api.inventorystock.model.entity.AppUser;
import com.api.inventorystock.model.entity.AppUserToken;
import com.api.inventorystock.model.request.*;
import com.api.inventorystock.model.response.AppUserDto;
import com.api.inventorystock.model.response.MailResponse;
import com.api.inventorystock.repository.AppUserRepository;
import com.api.inventorystock.security.JwtService;
import com.api.inventorystock.service.AppUserService;
import com.api.inventorystock.service.AppUserTokenService;
import com.api.inventorystock.service.AuthService;
import com.api.inventorystock.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AppUserService appUserService;
    private final AppUserTokenService tokenService;
    private final AuthenticationManager authentication;
    private final JwtService jwtService;
    private final EmailUtil mail;
    private final CacheManager cacheManager;
    private final AppUserRepository appUserRepository;

    private String generateOtp() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    @Override
    public AppUserDto create(AppUserAddRequest request) {
        return appUserService.create(request);
    }

    @Override
    public TokenResponse login(AuthRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        try {
            authentication.authenticate(new UsernamePasswordAuthenticationToken(email, request.getPassword()));
        } catch (AuthenticationException e) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }

        AppUser user = (AppUser) appUserService.loadUserByUsername(email);
        TokenResponse token = jwtService.generateAccess(user);

        AppUserToken appUserToken = new AppUserToken();
        appUserToken.setRefresh(token.getRefresh());
        appUserToken.setUser(user);

        tokenService.save(appUserToken);

        return token;
    }

    @Override
    public TokenResponse refresh(RefreshTokenRequest request) {
        String username = jwtService.extractUsername(request.getRefresh());

        UserDetails user = appUserService.loadUserByUsername(username);

        if (!jwtService.validateRefreshToken(request.getRefresh(), user)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED, MessageConstant.INVALID_REFRESH_TOKEN.getDefaultMessage());
        }

        return jwtService.generateRefresh(user);
    }

    @Override
    public MailResponse sendOtp(MailRequest.SendOtp request) {
        Cache cache = Objects.requireNonNull(cacheManager.getCache("otp"));
        String otp = generateOtp();
        String email = request.getTo().trim();

        try {
            mail.send(email, otp);
            cache.put(email, otp);
        } catch (Exception e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to send OTP email");
        }

        String expireAt = Instant.now().plus(Duration.ofMinutes(5)).toString();
        return new MailResponse(request.getTo().trim(), expireAt, 60);
    }

    @Override
    public void verifyOtp(MailRequest.VerifyOtp request) {
        try{
            Cache cache = Objects.requireNonNull(cacheManager.getCache("otp"));
            String email = String.valueOf(request.getTo());
            String otp = String.valueOf(request.getOtp());

            String hasOtp = cache.get(email, String.class);

            if(hasOtp == null){
                throw new CustomException(
                        HttpStatus.BAD_REQUEST,
                        "Send OTP first before verifying."
                );
            }

            if (!Objects.equals(hasOtp, otp)) {
                throw new CustomException(HttpStatus.BAD_REQUEST, "OTP expired or not found");
            }

        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        try{
            Cache cache = Objects.requireNonNull(cacheManager.getCache("otp"));
            String email = request.getTo();
            String password = request.getPassword();
            String confirm = request.getConfirm();

            String getOtp = cache.get(email, String.class);
            if (getOtp == null) {
                throw new CustomException(
                        HttpStatus.BAD_REQUEST,
                        "Send OTP first before reset password"
                );
            }

            if(!Objects.equals(password, confirm)){
                throw new CustomException(HttpStatus.BAD_REQUEST, "password miss match.");
            }

            AppUser user = appUserRepository.findByEmail(email).orElseThrow(
                    () -> new CustomException(HttpStatus.BAD_REQUEST, "email not found.")
            );
            user.setPassword(password);
            appUserRepository.save(user);
        } catch (Exception e){
            throw new CustomException(e.getMessage());
        }
    }
}
