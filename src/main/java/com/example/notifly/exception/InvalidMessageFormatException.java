package com.example.notifly.exception;

public class InvalidMessageFormatException extends RuntimeException{

    public InvalidMessageFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
