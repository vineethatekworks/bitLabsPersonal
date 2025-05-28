package com.talentstream.exception;

public class InterviewException extends Exception {
    public InterviewException(String message) {
        super(message);
    }
    
    public InterviewException(String message, Throwable cause) {
        super(message, cause);
    }
}