package fr.uga.l3miage.integrator.exceptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


@Data
@Builder
public class EditDayErrorResponse {

    @Schema(description = "end point call", example = "/api/v3.0/")
    private final String uri;
    @Schema(description = "error message", example = "Day cannot be edited !")
    private final String errorMessage;
}
