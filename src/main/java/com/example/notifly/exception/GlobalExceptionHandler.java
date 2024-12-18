package com.example.notifly.exception;

import com.example.notifly.dto.ErrorResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Resource Not Found Exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Resource Not Found", ex.getMessage());
    }

    // No Contacts Found Exception
    @ExceptionHandler(NoContactsFoundException.class)
    public ResponseEntity<Object> handleNoContactsFoundException(NoContactsFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "No Contacts Found", ex.getMessage());
    }

    // Invalid Message Format Exception
    @ExceptionHandler(InvalidMessageFormatException.class)
    public ResponseEntity<Object> handleInvalidMessageFormat(InvalidMessageFormatException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Message Format", ex.getMessage());
    }

    // Missing Data Exception
    @ExceptionHandler(MissingDataException.class)
    public ResponseEntity<Object> handleMissingData(MissingDataException ex) {
        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Missing Data", ex.getMessage());
    }

    @ExceptionHandler(NotificationSendException.class)
    public ResponseEntity<Object> handleNotificationSendException(NotificationSendException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Notification Send Error", ex.getMessage());
    }

    // Service Unavailable Exception
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<Object> handleServiceUnavailable(ServiceUnavailableException ex) {
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", ex.getMessage());
    }

    // Generic Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }

    // Batch Job Execution Exception
    @ExceptionHandler(JobExecutionException.class)
    public ResponseEntity<Object> handleJobExecutionException(JobExecutionException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Batch Job Execution Error", ex.getMessage());
    }

    @ExceptionHandler(BatchJobExecutionException.class)
    public ResponseEntity<Object> handleBatchJobExecutionException(BatchJobExecutionException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Batch Job Execution Error", ex.getMessage());
    }


    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String error, String message) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                LocalDateTime.now(),
                status.value(),
                error,
                message
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}
