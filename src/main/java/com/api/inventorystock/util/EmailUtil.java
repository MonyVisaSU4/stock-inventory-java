package com.api.inventorystock.util;

import com.api.inventorystock.common.configuration.AppProperty;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class EmailUtil {
    private final JavaMailSender sender;
    private final SpringTemplateEngine engine;
    private final AppProperty appProperty;

    @Async("emailExecutor")
    public void send(String to, String otp) throws MessagingException, UnsupportedEncodingException {
        Context context = new Context();
        context.setVariable("otp", otp);

        String html = engine.process("send-otp.html", context);
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                true,
                StandardCharsets.UTF_8.name());
        helper.setTo(to);
        helper.setText(html, true);
        helper.setSubject("Your Spring boot Verification OTP");
        String senderEmail = (appProperty != null && appProperty.getMail() != null
                && appProperty.getMail().getUsername() != null)
                ? appProperty.getMail().getUsername()
                : "kailh9235@gmail.com";
        helper.setFrom(new InternetAddress(senderEmail, "SETEC Institute"));
        helper.setSentDate(new Date());

        sender.send(message);
    }
}
