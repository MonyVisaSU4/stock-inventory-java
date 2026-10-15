package com.api.inventorystock.security;

import com.api.inventorystock.exception.CustomException;
import com.api.inventorystock.model.entity.AppUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {
    public AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof AppUser user)) {
            throw new CustomException(HttpStatus.UNAUTHORIZED,"No authenticated user in context");
        }
        return user;
    }
}
