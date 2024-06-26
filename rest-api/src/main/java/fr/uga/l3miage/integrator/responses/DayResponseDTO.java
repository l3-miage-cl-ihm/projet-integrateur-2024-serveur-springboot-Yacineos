package fr.uga.l3miage.integrator.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Schema(description = "Day representation")
public class DayResponseDTO {
    @Schema(description = "Tour list ")
    private List<TourPlannerResponseDTO> tours ;

    @Schema(description = "date of the day date format : 'yyyy-MM-dd'",example = "2024-05-10")
    private LocalDate date ;

}
