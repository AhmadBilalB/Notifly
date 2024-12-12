package com.example.notifly.controller;

import com.example.notifly.entity.EmailConfig;
import com.example.notifly.service.EmailConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/email-config")
@Tag(name = "Email Configuration", description = "Endpoints for managing email configuration")
public class EmailConfigController {

    @Autowired
    private EmailConfigService emailConfigService;

    @GetMapping
    @Operation(summary = "Get Email Configuration", description = "Fetches the current email configuration.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email configuration retrieved successfully."),
            @ApiResponse(responseCode = "404", description = "Email configuration not found.")
    })
    public ResponseEntity<EmailConfig> getEmailConfig() {
        Optional<EmailConfig> emailConfig = emailConfigService.getEmailConfig();
        return emailConfig.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping
    @Operation(summary = "Update Email Configuration", description = "Updates the sender email and token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email configuration updated successfully.")
    })
    public ResponseEntity<EmailConfig> updateEmailConfig(
            @RequestParam String senderEmail,
            @RequestParam String emailToken) {
        EmailConfig updatedConfig = emailConfigService.updateEmailConfig(senderEmail, emailToken);
        return ResponseEntity.ok(updatedConfig);
    }
}
