package com.example.notifly.controller;

import com.example.notifly.dto.NotificationEvent;
import com.example.notifly.service.NotificationService;
import com.example.notifly.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final WebSocketService webSocketService;
    private final NotificationService notificationService;

    /**
     * Sends a WebSocket notification.
     *
     * @param destination the WebSocket destination (e.g., topic or user).
     * @param message     the notification message.
     */
    @PostMapping("/send")
    public void sendNotification(@RequestParam String destination, @RequestParam String message) {
        webSocketService.sendNotification(destination, message);
    }

    /**
     * Sends an email notification to a contact using dynamic token-based authorization.
     *
     * @param contactId         the ID of the contact to notify.
     * @param username          the username of the user for fetching the token.
     * @param password          the password of the user for fetching the token.
     * @param notificationEvent the notification event containing details.
     */
    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmailNotification(
            @RequestParam String contactId,
            @RequestParam String username,
            @RequestParam String password,
            @RequestBody NotificationEvent notificationEvent) {
        // Set the recipient dynamically
        notificationEvent.setRecipient(contactId);

        // Send email notification with the provided username and password
        notificationService.saveAndSendNotification(notificationEvent, username, password);

        return ResponseEntity.ok("Email notification sent successfully.");
    }
}
