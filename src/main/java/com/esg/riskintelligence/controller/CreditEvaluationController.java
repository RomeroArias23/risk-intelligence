package com.esg.riskintelligence.controller;

import com.esg.riskintelligence.dto.CreditApplicationRequest;
import com.esg.riskintelligence.dto.CreditEvaluationResponse;
import com.esg.riskintelligence.service.EsgScoringService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/credit-evaluations")
public class CreditEvaluationController {

    private final EsgScoringService esgScoringService;

    public CreditEvaluationController(EsgScoringService esgScoringService) {
        this.esgScoringService = esgScoringService;
    }

    @PostMapping
    public ResponseEntity<CreditEvaluationResponse> evaluate(
            @Valid @RequestBody CreditApplicationRequest request) {
        CreditEvaluationResponse response = esgScoringService.evaluate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<CreditEvaluationResponse> findAll() {
        return esgScoringService.findAll();
    }

    @GetMapping("/{id}")
    public CreditEvaluationResponse findById(@PathVariable UUID id) {
        return esgScoringService.findById(id);
    }
}