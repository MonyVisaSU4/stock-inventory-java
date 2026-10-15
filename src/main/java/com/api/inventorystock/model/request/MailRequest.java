package com.api.inventorystock.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

public class MailRequest {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendOtp {
        @NotBlank(message = "Email Can not be blank")
        @Email(message = "Email must be valid")
        private String to;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class VerifyOtp {
        @NotBlank(message = "Email Can not be blank")
        @Email(message = "Email must be valid")
        private String to;

        @NotBlank(message = "Otp Can not be blank")
        private String otp;
    }
}
