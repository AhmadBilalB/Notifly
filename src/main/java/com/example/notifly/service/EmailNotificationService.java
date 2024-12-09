package com.example.notifly.service;

import com.example.notifly.dto.ContactDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    public boolean sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
