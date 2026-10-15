package com.api.inventorystock.model.response;

import com.api.inventorystock.common.enumerate.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDto {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private Roles role;
}
