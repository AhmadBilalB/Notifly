package com.example.notifly.service;

import com.example.notifly.entity.EmailConfig;
import com.example.notifly.repository.EmailConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailConfigService {


    private final EmailConfigRepository emailConfigRepository;

    EmailConfigService(EmailConfigRepository emailConfigRepository) {
        this.emailConfigRepository = emailConfigRepository;
    }


    public Optional<EmailConfig> getEmailConfig() {
        return emailConfigRepository.findFirstByOrderByIdAsc();
    }

    public EmailConfig updateEmailConfig(String senderEmail, String emailToken) {
        EmailConfig emailConfig = emailConfigRepository.findFirstByOrderByIdAsc()
                .orElse(new EmailConfig());
        emailConfig.setSenderEmail(senderEmail);
        emailConfig.setEmailToken(emailToken);
        return emailConfigRepository.save(emailConfig);
    }

    public boolean isConfigured() {
        Optional<EmailConfig> config = getEmailConfig();
        return config.isPresent() && config.get().getSenderEmail() != null && !config.get().getSenderEmail().isEmpty()
                && config.get().getEmailToken() != null && !config.get().getEmailToken().isEmpty();
    }
}
