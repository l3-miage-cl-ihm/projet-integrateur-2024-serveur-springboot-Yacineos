package fr.uga.l3miage.integrator.requests;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@Schema(description = "Day planification")
public class DayCreationRequest {
    @Schema(description = "Date of the day we want plan ", example = "2024-04-30")
    private final LocalDate date;
    @Schema(description = "Day tour list ")
    private final List<TourCreationRequest> tours;
}
