package com.api.inventorystock.service.impl;

import com.api.inventorystock.model.entity.AppUser;
import com.api.inventorystock.model.entity.AppUserToken;
import com.api.inventorystock.repository.AppUserTokenRepository;
import com.api.inventorystock.service.AppUserTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppUserTokenServiceImpl implements AppUserTokenService {
    private final AppUserTokenRepository appUserTokenRepository;

    @Override
    public void save(AppUserToken token) {
        appUserTokenRepository.save(token);
    }

    @Override
    public Optional<List<AppUserToken>> findAllByUser(AppUser user) {
        return appUserTokenRepository.findAllByUser(user);
    }

    @Override
    public void deleteAll(List<AppUserToken> tokens) {
        appUserTokenRepository.deleteAll(tokens);
    }
}
