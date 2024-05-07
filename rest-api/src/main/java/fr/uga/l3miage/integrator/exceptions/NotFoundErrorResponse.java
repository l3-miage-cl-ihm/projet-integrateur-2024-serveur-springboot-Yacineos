package fr.uga.l3miage.integrator.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class NotFoundErrorResponse {
    @Schema(description = "end point call", example = "/api/v2.0/")
    private final String uri;
    @Schema(description = "error message", example = "Entity not found ")
    private final String errorMessage;
}