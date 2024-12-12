package com.example.notifly.exception;

public class BatchJobExecutionException extends RuntimeException {
    public BatchJobExecutionException(String message) {
        super(message);
    }

    public BatchJobExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
