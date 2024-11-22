package com.example.notifly.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEvent {
    private String eventType;
    private String recipient;
    private String payload;
}