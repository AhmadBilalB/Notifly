package com.example.notifly.kafka;


import com.example.notifly.dto.ContactDTO;
import com.example.notifly.dto.NotificationEvent;
import com.example.notifly.exception.InvalidMessageFormatException;
import com.example.notifly.exception.MissingDataException;

import com.example.notifly.exception.ServiceUnavailableException;
import com.example.notifly.service.EmailConfigService;
import com.example.notifly.service.EmailTemplateService;
import com.example.notifly.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class EventConsumer {

    private final EmailTemplateService emailTemplateService;
    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;
    private final EmailConfigService emailConfigService;




    public EventConsumer(EmailTemplateService emailTemplateService, NotificationService notificationService, EmailConfigService emailConfigService) {
        this.emailTemplateService = emailTemplateService;
        this.objectMapper = new ObjectMapper();
        this.notificationService = notificationService;
        this.emailConfigService = emailConfigService;
    }

    @KafkaListener(topics = "new-contacts", groupId = "notification-group")
    public void consume(String message, Acknowledgment acknowledgment) {


        try{
            log.info("Received new contact: {}", message);

            if (!emailConfigService.isConfigured()) {
                log.error("Email configuration is not set. Skipping message processing.");
                acknowledgment.nack(Duration.ofSeconds(10));
                return; // Skip processing if configuration is not set
            }
            ContactDTO contactDTO = objectMapper.readValue(message, ContactDTO.class);
            log.info("Parsed Contact: {}", contactDTO);

            if (contactDTO.getName() == null || contactDTO.getEmail() == null) {
                throw new MissingDataException("Contact name or email is missing.");
            }

            String payloadMessage = emailTemplateService.getNewContactTemplate(contactDTO.getName());
            log.info("Generated email payload: {}", payloadMessage);

            if (payloadMessage == null) {
                log.error("Failed to generate email payload for contact: {}", contactDTO);
                throw new ServiceUnavailableException("Failed to generate email payload for contact.");
            }else {
                NotificationEvent event = NotificationEvent.builder()
                        .eventType("New_Contact")
                        .recipient(contactDTO.getEmail())
                        .payload(payloadMessage)
                        .build();

                List<ContactDTO> contacts = new ArrayList<>();
                contacts.add(contactDTO);
                notificationService.sendNotificationToContacts(event, contacts);
            }
            acknowledgment.acknowledge(); // Acknowledge on success
        } catch (JsonProcessingException e) {
            log.error("Invalid JSON format: {}", e.getMessage(), e);
            acknowledgment.nack(Duration.ofSeconds(10)); // Negative acknowledgment with retry
            throw new InvalidMessageFormatException("Message has an invalid JSON format.", e);

        } catch (MissingDataException e) {
            log.error("Missing required data in message: {}", e.getMessage(), e);
            acknowledgment.nack(Duration.ofSeconds(10)); // Negative acknowledgment with retry
            throw e;
        } catch (ServiceUnavailableException e) {
            log.error("Unexpected error while processing message: {}", e.getMessage(), e);
            acknowledgment.nack(Duration.ofSeconds(10)); // Negative acknowledgment with retry
            throw new ServiceUnavailableException("An unexpected error occurred while processing the message.", e);
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
            acknowledgment.nack(Duration.ofSeconds(10)); // Negative acknowledgment with retry
        }
    }
}
