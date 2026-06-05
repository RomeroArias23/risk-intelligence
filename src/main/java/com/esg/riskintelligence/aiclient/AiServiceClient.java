package com.esg.riskintelligence.aiclient;

import com.esg.riskintelligence.error.AiServiceUnavailableException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Component
public class AiServiceClient {

    private final RestClient restClient;

    public AiServiceClient(RestClient aiServiceRestClient) {
        this.restClient = aiServiceRestClient;
    }

    public ScoringResponse score(ScoringRequest request) {
        try {
            return restClient.post()
                    .uri("/api/v1/score")
                    .body(request)
                    .retrieve()
                    .body(ScoringResponse.class);

        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            // The AI service responded with 4xx or 5xx.
            HttpStatusCode status = ex.getStatusCode();
            throw new AiServiceUnavailableException(
                    "AI service returned error status " + status.value(),
                    AiServiceUnavailableException.Cause.UPSTREAM_ERROR,
                    ex);

        } catch (RestClientException ex) {
            // Catches ResourceAccessException and any other transport/parsing
            // failure. We unwrap the cause chain to distinguish timeout
            // from connection failure from anything else.
            Throwable rootCause = findRootCause(ex);

            if (rootCause instanceof SocketTimeoutException) {
                throw new AiServiceUnavailableException(
                        "AI service timed out",
                        AiServiceUnavailableException.Cause.TIMEOUT,
                        ex);
            }
            if (rootCause instanceof ConnectException) {
                throw new AiServiceUnavailableException(
                        "AI service unreachable",
                        AiServiceUnavailableException.Cause.CONNECTION_FAILURE,
                        ex);
            }
            // Unknown transport-layer failure; treat as upstream error.
            throw new AiServiceUnavailableException(
                    "AI service communication failure: " + ex.getMessage(),
                    AiServiceUnavailableException.Cause.UPSTREAM_ERROR,
                    ex);
        }
    }

    private Throwable findRootCause(Throwable t) {
        Throwable cause = t;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }
}