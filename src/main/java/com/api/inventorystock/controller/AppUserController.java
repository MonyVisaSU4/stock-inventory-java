package com.api.inventorystock.controller;

import com.api.inventorystock.common.response.SuccessResponse;
import com.api.inventorystock.model.request.AppUserUpdateRequest;
import com.api.inventorystock.model.response.AppUserDto;
import com.api.inventorystock.service.AppUserService;
import com.api.inventorystock.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user-app")
@RequiredArgsConstructor
public class AppUserController {
    private final AppUserService appUserService;

    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<Void>> logout() {
        appUserService.logout();
        return ResponseEntity.ok(
                ApiResponseUtil.success(HttpStatus.OK)
        );
    }

    @GetMapping("/current")
    public ResponseEntity<SuccessResponse<AppUserDto>> current() {
        return ResponseEntity.ok(
                ApiResponseUtil.success(HttpStatus.OK, appUserService.current())
        );
    }

    @PutMapping
    public ResponseEntity<SuccessResponse<AppUserDto>> update(@Valid @RequestBody AppUserUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponseUtil.success(HttpStatus.OK, appUserService.update(request))
        );
    }
}
