package com.example.notifly.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic notiflyNotificationTopic() {
        return new NewTopic("notifly-notification", 1, (short) 1); // 1 partition, 1 replica
    }
}
