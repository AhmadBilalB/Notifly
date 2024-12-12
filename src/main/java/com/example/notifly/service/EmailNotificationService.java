package com.example.notifly.service;

import com.example.notifly.dto.ContactDTO;
import com.example.notifly.entity.EmailConfig;
import com.example.notifly.exception.NotificationSendException;
import com.example.notifly.repository.EmailConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    private final EmailConfigRepository emailConfigRepository;

    public boolean sendEmail(String to, String subject, String body) {
        try {
            // Fetch sender email from the database
            Optional<EmailConfig> emailConfigOptional = emailConfigRepository.findFirstByOrderByIdAsc();

            if (emailConfigOptional.isEmpty()) {
                throw new NotificationSendException("Sender email configuration is not set.");
            }

            EmailConfig emailConfig = emailConfigOptional.get();

            // Check if email and email token are set
            if (emailConfig.getSenderEmail() == null || emailConfig.getSenderEmail().isEmpty()) {
                throw new NotificationSendException("Sender email is not configured.");
            }
            if (emailConfig.getEmailToken() == null || emailConfig.getEmailToken().isEmpty()) {
                throw new NotificationSendException("Email token is not configured.");
            }

            // Configure JavaMailSender dynamically
            JavaMailSenderImpl dynamicMailSender = new JavaMailSenderImpl();
            dynamicMailSender.setHost("smtp.gmail.com");
            dynamicMailSender.setPort(587);
            dynamicMailSender.setUsername(emailConfig.getSenderEmail());
            dynamicMailSender.setPassword(emailConfig.getEmailToken()); // Use token for authentication

            Properties mailProperties = dynamicMailSender.getJavaMailProperties();
            mailProperties.put("mail.transport.protocol", "smtp");
            mailProperties.put("mail.smtp.auth", "true");
            mailProperties.put("mail.smtp.starttls.enable", "true");
            mailProperties.put("mail.smtp.starttls.required", "true");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailConfig.getSenderEmail());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            // Send the email
            dynamicMailSender.send(message);

            return true;
        } catch (NotificationSendException ex) {
            // Log and rethrow custom exception
            throw ex;
        } catch (Exception e) {
            // Handle other unexpected errors
            throw new NotificationSendException("Failed to send email: " + e.getMessage(), e);
        }
    }

    public void sendNotifications(List<ContactDTO> contacts) {
        for (ContactDTO contact : contacts) {
            // You can customize the email body and subject as needed
            String subject = "Important Notification";
            String body = "Hello " + contact.getName() + ", this is your notification.";

            // Call sendEmail to send the email to each contact
            boolean emailSent = sendEmail(contact.getEmail(), subject, body);

            // You can log or handle the result of email sending if needed
            if (emailSent) {
                System.out.println("Email sent to: " + contact.getEmail());
            } else {
                System.out.println("Failed to send email to: " + contact.getEmail());
            }
        }
    }
}
