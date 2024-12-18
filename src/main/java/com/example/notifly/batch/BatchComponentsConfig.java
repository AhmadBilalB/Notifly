package com.example.notifly.batch;

import com.example.notifly.client.ContactlyClient;
import com.example.notifly.dto.ContactDTO;
import com.example.notifly.service.EmailNotificationService;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Configuration
public class BatchComponentsConfig {

    @Bean
    @StepScope
    public ItemReader<List<ContactDTO>> itemReader(ContactlyClient contactlyClient,@Value("#{jobParameters['jwtToken']}") String jwtToken) {

        //return contactlyClient::fetchContactByGettingToken;
        return new ItemReader<>() {
            private final List<ContactDTO> contacts = contactlyClient.fetchContactByGettingToken(jwtToken);
            private int nextIndex = 0;
            private static final int BATCH_SIZE = 10; // Define your batch size here

            @Override
            public List<ContactDTO> read() {

                if (nextIndex < contacts.size()) {
                    int endIndex = Math.min(nextIndex + BATCH_SIZE, contacts.size());
                    List<ContactDTO> batch = contacts.subList(nextIndex, endIndex);
                    nextIndex = endIndex;
                    return batch;
                } else {
                    return null; // No more data
                }
            }
        };
    }


    @Bean
    public ItemProcessor<List<ContactDTO>, List<ContactDTO>> itemProcessor() {
        return contacts -> {
            // Add any processing logic here, e.g., filtering contacts
            return contacts; // Pass contacts as-is for now
        };
    }

    @Bean
    public ItemWriter<List<ContactDTO>> itemWriter(EmailNotificationService emailNotificationService) {
        return contacts -> {
            // Implement sending email notifications
            for (List<ContactDTO> batch : contacts) {
                emailNotificationService.sendNotifications(batch);
            }
        };
    }
}

