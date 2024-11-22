package com.example.notifly.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailNotificationRequest {
    private String recipient;
    private String subject;
    private String message;
}