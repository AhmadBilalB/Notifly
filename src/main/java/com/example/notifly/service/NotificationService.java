package com.example.notifly.service;

import com.example.notifly.client.ContactlyClient;
import com.example.notifly.dto.ContactDTO;
import com.example.notifly.dto.NotificationEvent;
import com.example.notifly.entity.Notification;
import com.example.notifly.enums.NotificationType;
import com.example.notifly.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailNotificationService emailNotificationService;
    private final ContactlyClient contactlyClient;

    public Notification saveAndSendNotification(NotificationEvent event, String username, String password) {
        log.info("Received notification event: {}", event);

        List<ContactDTO> contacts = contactlyClient.fetchAllContacts(username, password);
        log.info("Fetched contact details: {}", contacts);

        // Assuming you're sending the notification to the first contact in the list
        if (contacts.isEmpty()) {
            log.error("No contacts found for user: {}", username);
            throw new IllegalStateException("No contacts found for user: " + username);
        }

        ContactDTO contact = contacts.get(0); // Get the first contact

        Notification notification = Notification.builder()
                .recipient(contact.getEmail()) // Get email from the first contact
                .message(event.getPayload())
                .type(NotificationType.EMAIL)
                .sent(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);
        log.info("Notification saved: {}", savedNotification);

        boolean isSent = emailNotificationService.sendEmail(
                contact.getEmail(),
                "Notification Alert",
                event.getPayload()
        );
        log.info("Email sent status: {}", isSent);

        savedNotification.setSent(isSent);
        return notificationRepository.save(savedNotification);
    }
}
