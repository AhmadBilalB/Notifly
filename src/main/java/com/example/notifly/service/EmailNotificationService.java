package com.example.notifly.service;


import com.example.notifly.dto.EmailNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    public void sendEmail(EmailNotificationRequest request) {
        log.info("Sending email to: {} with subject: {}", request.getRecipient(), request.getSubject());
        // Implement email-sending logic
    }
}
