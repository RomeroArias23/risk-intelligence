package com.esg.riskintelligence.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreditApplicationRequest(

        @NotBlank(message = "Company name is required")
        @Size(max = 200, message = "Company name must be at most 200 characters")
        String companyName,

        @DecimalMin(value = "0.01", message = "Requested amount must be greater than zero")
        double requestedAmount,

        @Min(value = 0, message = "Environmental score must be between 0 and 100")
        @Max(value = 100, message = "Environmental score must be between 0 and 100")
        int environmentalScore,

        @Min(value = 0, message = "Social score must be between 0 and 100")
        @Max(value = 100, message = "Social score must be between 0 and 100")
        int socialScore,

        @Min(value = 0, message = "Governance score must be between 0 and 100")
        @Max(value = 100, message = "Governance score must be between 0 and 100")
        int governanceScore
) {
}
