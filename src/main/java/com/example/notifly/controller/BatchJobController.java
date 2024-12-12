package com.example.notifly.controller;

import com.example.notifly.exception.BatchJobExecutionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;

@RestController
@RequestMapping("/batch")
@Tag(name = "Batch Jobs", description = "Endpoints for triggering batch jobs")
@SecurityRequirement(name = "Bearer") // Ensures that the authorization is included in Swagger UI
public class BatchJobController {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job emailJob;

    /**
     * Endpoint for triggering the email batch job. It expects a Bearer token
     * for authorization in the request header.
     */
    @GetMapping("/trigger-email-batch")
    @Operation(
            summary = "Trigger Email Batch Job",
            description = "Triggers a batch job for processing email notifications.",
            security = @SecurityRequirement(name = "Bearer") // Swagger UI will expect this token for authentication
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Batch job triggered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid authorization token"),
            @ApiResponse(responseCode = "500", description = "Failed to execute batch job due to an internal error")
    })
    public String triggerEmailBatch(@RequestHeader(HttpHeaders.AUTHORIZATION)
                                    @Parameter(description = "Bearer token for authentication", required = true)
                                    String authorizationHeader) {
        try {
            // Ensure that the token is in the correct format (starts with "Bearer ")
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                throw new BatchJobExecutionException("Invalid or missing authorization token.");
            }

            // Extract the token from the header
            String token = authorizationHeader.replace("Bearer ", "");

            // Add the token to job parameters
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("jwtToken", token) // Add the token as a parameter to the job
                    .toJobParameters();

            // Launch the batch job with the token as a parameter
            jobLauncher.run(emailJob, jobParameters);
            return "Batch job triggered successfully.";
        } catch (BatchJobExecutionException e) {
            // Provide specific error message when token validation fails
            throw e;
        } catch (Exception e) {
            // Handle any other errors and provide a proper error message
            throw new BatchJobExecutionException("Failed to execute batch job: " + e.getMessage(), e);
        }
    }
}
