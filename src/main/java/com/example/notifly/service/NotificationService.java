package com.example.notifly.service;

import com.example.notifly.client.ContactlyClient;
import com.example.notifly.dto.ContactDTO;
import com.example.notifly.dto.NotificationEvent;
import com.example.notifly.entity.Notification;
import com.example.notifly.enums.NotificationType;
import com.example.notifly.exception.NoContactsFoundException;
import com.example.notifly.exception.NotificationSendException;
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

    /**
     * Saves and sends a notification based on the provided event and user credentials.
     *
     * @param event    the notification event containing the payload.
     * @param username the username for authentication.
     * @param password the password for authentication.
     * @return the saved Notification.
     */
    public Notification saveAndSendNotification(NotificationEvent event, String username, String password) {
        log.info("Received notification event: {}", event);

        // Fetch contacts for the given user
        List<ContactDTO> contacts = fetchContacts(username, password);

        // Send notifications to the fetched contacts
        return sendNotificationToContacts(event, contacts);
    }

    /**
     * Fetches contacts for the provided user credentials.
     *
     * @param username the username for authentication.
     * @param password the password for authentication.
     * @return a list of ContactDTO.
     */
    private List<ContactDTO> fetchContacts(String username, String password) {
        try {
            List<ContactDTO> contacts = contactlyClient.fetchAllContacts(username, password);
            if (contacts.isEmpty()) {
                log.error("No contacts found for user: {}", username);
                throw new NoContactsFoundException("No contacts found for user: " + username);
            }
            log.info("Fetched contacts: {}", contacts);
            return contacts;
        } catch (Exception ex) {
            log.error("Failed to fetch contacts for user: {}", username, ex);
            throw new NoContactsFoundException("Unable to fetch contacts for user: " + username, ex);
        }
    }

    /**
     * Sends a notification to a list of contacts and saves their statuses.
     *
     * @param event    the notification event containing the payload.
     * @param contacts the list of contacts to notify.
     * @return the saved Notification for the last contact (for simplicity).
     */
    public Notification sendNotificationToContacts(NotificationEvent event, List<ContactDTO> contacts) {
        Notification lastSavedNotification = null;

        for (ContactDTO contact : contacts) {
            try {
                Notification notification = Notification.builder()
                        .recipient(contact.getEmail())
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
                log.info("Email sent status for {}: {}", contact.getEmail(), isSent);

                savedNotification.setSent(isSent);
                lastSavedNotification = notificationRepository.save(savedNotification);
            } catch (Exception ex) {
                log.error("Failed to send notification to contact: {}", contact.getEmail(), ex);
                throw new NotificationSendException("Failed to send notification to contact: " + contact.getEmail(), ex);
            }
        }

        return lastSavedNotification; // Return the last saved notification (or adapt as needed).
    }
}
