package org.example.admin;

public class InvalidAccountIdException extends Exception {
    public String reason;
    public InvalidAccountIdException(String errorMessage) {
        super(errorMessage);
        reason = errorMessage;
    }
}
