package fr.uga.l3miage.integrator.exceptions;

import fr.uga.l3miage.integrator.enums.DeliveryState;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeliveryStatusNotUpdatedResponse {
    @Schema(description = "error message", example = "Cannot update delivery state ")
    private String errorMessage;
    @Schema(description = "end point call", example = "/api/v3.0/")
    private String uri;
}


