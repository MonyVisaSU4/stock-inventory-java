package com.api.inventorystock.service;

import com.api.inventorystock.model.request.AppUserAddRequest;
import com.api.inventorystock.model.request.AppUserUpdateRequest;
import com.api.inventorystock.model.response.AppUserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AppUserService extends UserDetailsService {
    AppUserDto create(AppUserAddRequest request);
    AppUserDto update(AppUserUpdateRequest request);
    AppUserDto current();
    void logout();
}
