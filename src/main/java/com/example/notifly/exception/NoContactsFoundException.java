package com.example.notifly.exception;

public class NoContactsFoundException extends RuntimeException {
  public NoContactsFoundException(String message) {
    super(message);
  }
  public NoContactsFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
