package com.example.notifly.service;

import com.example.notifly.dto.NotificationEvent;
import com.example.notifly.entity.Notification;
import com.example.notifly.enums.NotificationType;
import com.example.notifly.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public Notification saveNotification(NotificationEvent event) {
        Notification notification = Notification.builder()
                .recipient(event.getRecipient())
                .message(event.getPayload())
                .type(NotificationType.EMAIL)
                .sent(false)
                .build();
        return notificationRepository.save(notification);
    }
}
