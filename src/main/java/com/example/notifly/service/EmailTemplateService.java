package com.example.notifly.service;

public interface EmailTemplateService {
    String getNewContactTemplate(String contactName);
    String getGeneralNotificationTemplate(String recipientName);
    String getFallbackTemplate();
}
