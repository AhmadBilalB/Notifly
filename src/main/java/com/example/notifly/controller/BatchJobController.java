package com.example.notifly.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch") // Main path for the controller
public class BatchJobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job emailJob; // This should match the job defined in your BatchConfig

    @GetMapping("/trigger-email-batch")
    public String triggerEmailBatch() {
        try {
            // Trigger the batch job
            jobLauncher.run(emailJob, new org.springframework.batch.core.JobParameters());
            return "Batch job triggered successfully.";
        } catch (JobExecutionException e) {
            // Handle JobExecutionException specifically for batch failures
            System.out.println("JobExecutionException: Failed to execute batch job - " + e.getMessage());
            return "Failed to execute batch job: " + e.getMessage();
        } catch (Exception e) {
            // Handle unexpected exceptions
            //System.out.println("Unexpected error while triggering batch job: " + e.getMessage());
            e.printStackTrace();
            return "Unexpected error: " + e.getMessage();
        }
    }
}
