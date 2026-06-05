package com.esg.riskintelligence.error;

public class AiServiceUnavailableException extends RuntimeException {

    public enum Cause {
        CONNECTION_FAILURE,
        UPSTREAM_ERROR,
        TIMEOUT
    }

    private final Cause cause;

    public AiServiceUnavailableException(String message, Cause cause, Throwable originalCause) {
        super(message, originalCause);
        this.cause = cause;
    }

    public Cause getFailureCause() {
        return cause;
    }
}