package com.example.notifly.batch;

import com.example.notifly.entity.Notification;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class NotificationProcessor implements ItemProcessor<Notification, Notification> {

    @Override
    public Notification process(Notification notification) throws Exception {
        // Add any processing logic here, such as formatting or validation
        System.out.println("Processing notification for: " + notification.getRecipient());
        return notification; // Return the processed notification
    }
}
