package fr.uga.l3miage.integrator.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Data
@Schema(description = "Day representation")
public class DayResponseDTO {
    @Schema(description = "Tour list ")
    private Set<TourPlannerResponseDTO> tours ;

    @Schema(description = "date of the day date format : 'yyyy-MM-dd'")
    private LocalDate date ;

}
