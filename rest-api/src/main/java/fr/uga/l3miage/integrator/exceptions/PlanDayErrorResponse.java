package fr.uga.l3miage.integrator.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PlanDayErrorResponse {
    @Schema(description = "end point call", example = "/api/v1.0/")
    private final String uri;
    @Schema(description = "error message", example = "Day cannot be planned ! ")
    private final String errorMessage;

}


