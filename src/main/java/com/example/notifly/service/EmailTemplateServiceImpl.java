package com.example.notifly.service;

import org.springframework.stereotype.Service;

@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    /**
     * Generates a default email template for a new contact event.
     *
     * @param contactName the name of the new contact.
     * @return a formatted email message.
     */
    public String getNewContactTemplate(String contactName) {
        return String.format(
                "Hello %s,\n\nA new contact has been added to your list. Welcome them now!",
                contactName
        );
    }

    /**
     * Generates a default email template for general notifications.
     *
     * @param recipientName the recipient's name.
     * @return a formatted email message.
     */
    public String getGeneralNotificationTemplate(String recipientName) {
        return String.format(
                "Hello %s,\n\nYou have a new notification. Please check your account for details.",
                recipientName
        );
    }

    /**
     * Generates a fallback or default email template.
     *
     * @return a generic message.
     */
    public String getFallbackTemplate() {
        return "Hello,\n\nYou have received a notification. Please check your account.";
    }
}
