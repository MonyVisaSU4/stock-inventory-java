package com.api.inventorystock.service;

import com.api.inventorystock.model.entity.AppUser;
import com.api.inventorystock.model.entity.AppUserToken;

import java.util.List;
import java.util.Optional;

public interface AppUserTokenService {
    void save(AppUserToken token);
    Optional<List<AppUserToken>> findAllByUser(AppUser user);
    void deleteAll(List<AppUserToken> tokens);
}
