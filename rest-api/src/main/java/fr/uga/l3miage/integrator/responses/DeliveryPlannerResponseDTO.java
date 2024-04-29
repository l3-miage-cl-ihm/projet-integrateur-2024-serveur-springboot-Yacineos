package fr.uga.l3miage.integrator.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "Delivery representation for planner")
public class DeliveryPlannerResponseDTO {
    @Schema(description = "order of the delivery")
    private Set<String> orders;
    @Schema(description = "distance to cover from last delivery or from warehouse")
    private Double distanceToCover;
}
