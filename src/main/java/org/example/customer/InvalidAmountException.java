package org.example.customer;

public class InvalidAmountException extends Exception {
    public String reason;

    public InvalidAmountException(String errorMessage) {
        super(errorMessage);
        reason = errorMessage;
    }
}
