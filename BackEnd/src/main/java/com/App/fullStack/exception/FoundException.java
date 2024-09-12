package com.App.fullStack.exception;

public class FoundException extends RuntimeException {
    public FoundException(String message) {
        super(message);
    }
    @Override
    public synchronized Throwable fillInStackTrace() {
        // Prevent the stack trace from being logged
        return this;
    }
    
}
