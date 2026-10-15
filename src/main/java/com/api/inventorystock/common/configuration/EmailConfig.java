package com.api.inventorystock.common.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@RequiredArgsConstructor
public class EmailConfig {
    private final AppProperty appProperty;

    @Bean
    public JavaMailSender sender(){
        AppProperty.Mail app = appProperty.getMail();
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(app.getHost());
        sender.setPort(app.getPort());
        sender.setUsername(app.getUsername());
        sender.setPassword(app.getPassword());
        sender.setJavaMailProperties(app.getProperties());
        return sender;
    }
}
