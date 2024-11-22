package com.example.notifly.batch;

import com.example.notifly.entity.Notification;
import com.example.notifly.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@StepScope
@RequiredArgsConstructor
public class EmailBatchJob implements Tasklet {

    private final NotificationRepository notificationRepository;

    @Override
    public RepeatStatus execute(StepContribution contribution, org.springframework.batch.core.scope.context.ChunkContext chunkContext) {
        List<Notification> unsentNotifications = notificationRepository.findAll(); // Fetch unsent notifications
        unsentNotifications.forEach(notification -> {
            // Logic to send email
            notification.setSent(true);
            notificationRepository.save(notification);
        });
        return RepeatStatus.FINISHED;
    }
}