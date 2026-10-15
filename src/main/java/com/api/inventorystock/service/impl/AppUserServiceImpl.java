package com.api.inventorystock.service.impl;

import com.api.inventorystock.common.constant.MessageConstant;
import com.api.inventorystock.exception.CustomException;
import com.api.inventorystock.model.entity.AppUser;
import com.api.inventorystock.model.request.AppUserAddRequest;
import com.api.inventorystock.model.request.AppUserUpdateRequest;
import com.api.inventorystock.model.response.AppUserDto;
import com.api.inventorystock.repository.AppUserRepository;
import com.api.inventorystock.security.CurrentUserService;
import com.api.inventorystock.service.AppUserService;
import com.api.inventorystock.service.AppUserTokenService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.modelmapper.ModelMapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final AppUserTokenService tokenService;
    private final CurrentUserService currentUserService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    @Override
    @NullMarked
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, MessageConstant.NOT_FOUND.getDefaultMessage()));
    }

    @Override
    public AppUserDto current() {
        return mapper.map(currentUserService.getCurrentUser(), AppUserDto.class);
    }

    @Override
    @Transactional
    public void logout() {
        AppUser user = currentUserService.getCurrentUser();

        tokenService.findAllByUser(user)
                .ifPresent(tokenService::deleteAll);

        SecurityContextHolder.clearContext();
    }

    @Override
    public AppUserDto create(AppUserAddRequest request) {
        AppUser user = mapper.map(request, AppUser.class);
        user.setPassword(
                passwordEncoder.encode(
                        this.confirm(request.getPassword(), request.getConfirm())
                )
        );
        return mapper.map(appUserRepository.save(user), AppUserDto.class);
    }

    @Override
    public AppUserDto update(AppUserUpdateRequest request) {
        AppUser user = appUserRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(HttpStatus.NOT_FOUND, MessageConstant.NOT_FOUND.getDefaultMessage()));
        mapper.map(request,user);
        user.setPassword(
                passwordEncoder.encode(
                        this.confirm(request.getPassword(), request.getConfirm())
                )
        );
        return mapper.map(appUserRepository.save(user), AppUserDto.class);
    }

    private String confirm(String password, String confirm) {
        if (!password.equals(confirm)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, MessageConstant.CONFIRM_PASSWORD_NOT_MATCH.getDefaultMessage());
        }
        return password;
    }
}
