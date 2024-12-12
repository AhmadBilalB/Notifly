package com.example.notifly.batch;

import com.example.notifly.dto.ContactDTO;
import com.example.notifly.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final EmailNotificationService emailNotificationService;

    @Bean
    public Job emailJob(JobRepository jobRepository, Step emailStep) {
        return new JobBuilder("emailJob", jobRepository)
                .start(emailStep)
                .build();
    }

    @Bean
    public Step emailStep(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager,
                          ItemReader<List<ContactDTO>> reader,
                          ItemProcessor<List<ContactDTO>, List<ContactDTO>> processor,
                          ItemWriter<List<ContactDTO>> writer) {
        return new StepBuilder("emailStep", jobRepository)
                .<List<ContactDTO>, List<ContactDTO>>chunk(2, transactionManager)  // Updated method
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor(); // A simple task executor
    }

    @Bean
    public JobRepository jobRepository(PlatformTransactionManager transactionManager, DataSource dataSource) throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.afterPropertiesSet();
        return factory.getObject();

    }


}


