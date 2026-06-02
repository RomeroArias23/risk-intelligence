package com.esg.riskintelligence.error;

import java.util.UUID;

public class EvaluationNotFoundException extends RuntimeException {

    public EvaluationNotFoundException(UUID id) {
        super("Evaluation not found: " + id);
    }
}