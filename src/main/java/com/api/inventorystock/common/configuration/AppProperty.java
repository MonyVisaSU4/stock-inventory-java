package com.api.inventorystock.common.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperty {
    private final Jwt jwt = new Jwt();
    private final Mail mail = new Mail();

    @Data
    public static class Jwt {
        private Long accessExpiration;
        private Long refreshExpiration;
        private String secret;
    }

    @Data
    public static class Mail {
        private String host;
        private int port;
        private String username;
        private String password;
        private Properties properties = new Properties();
    }
}
