package com.esg.riskintelligence.error;

public record FieldError(
        String field,
        String message
) {
}