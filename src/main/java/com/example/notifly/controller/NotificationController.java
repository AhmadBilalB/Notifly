package com.example.notifly.controller;

import com.example.notifly.dto.ErrorResponseDTO;
import com.example.notifly.dto.NotificationEvent;
import com.example.notifly.service.NotificationService;
import com.example.notifly.service.WebSocketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "APIs for sending and managing notifications")
public class NotificationController {

    private final WebSocketService webSocketService;
    private final NotificationService notificationService;

    /**
     * Sends a WebSocket notification.
     *
     * @param destination the WebSocket destination (e.g., topic or user).
     * @param message     the notification message.
     */
    @PostMapping("/send")
    @Operation(
            summary = "Send a WebSocket notification",
            description = "Sends a notification via WebSocket to a specific destination with the provided message.",
            parameters = {
                    @Parameter(name = "destination", description = "The WebSocket destination (e.g., topic or user)", required = true, in = ParameterIn.QUERY),
                    @Parameter(name = "message", description = "The notification message to send", required = true, in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Notification sent successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                            content = @Content(mediaType = "application/json",  schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    public void sendNotification(@RequestParam String destination, @RequestParam String message) {
        webSocketService.sendNotification(destination, message);
    }

    /**
     * Sends an email notification to a contact using dynamic token-based authorization.
     *
     //* @param contactId         the ID of the contact to notify.
     * @param username          the username of the user for fetching the token.
     * @param password          the password of the user for fetching the token.
     * @param notificationEvent the notification event containing details.
     */
    @PostMapping("/send-email")
    @Operation(
            summary = "Send an email notification",
            description = "Sends an email notification to a contact using dynamic token-based authorization.",
            parameters = {
                   // @Parameter(name = "contactId", description = "The ID of the contact to notify", required = true, in = ParameterIn.QUERY),
                    @Parameter(name = "username", description = "The username for fetching the authorization token", required = true, in = ParameterIn.QUERY),
                    @Parameter(name = "password", description = "The password for fetching the authorization token", required = true, in = ParameterIn.QUERY)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the notification event, including recipient and message details",
                    required = true,
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationEvent.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Email notification sent successfully",
                            content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Invalid request or missing parameters",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error while sending the email",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDTO.class)))
            }
    )
    public ResponseEntity<String> sendEmailNotification(
            @RequestParam String username,
            @RequestParam String password,
            @RequestBody NotificationEvent notificationEvent) {
        // Set the recipient dynamically
        //notificationEvent.setRecipient(contactId);

        // Send email notification with the provided username and password
        notificationService.saveAndSendNotification(notificationEvent, username, password);

        return ResponseEntity.ok("Email notification sent successfully.");
    }
}
