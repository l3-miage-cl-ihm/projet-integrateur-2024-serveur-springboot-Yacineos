package fr.uga.l3miage.integrator.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DayStateNotUpdatedResponse {
    @Schema(description = "error message", example = "Cannot update day state ")
    private String errorMessage;
    @Schema(description = "end point call", example = "/api/v3.0/planner/days/J131G/updateState")
    private String uri;
}
