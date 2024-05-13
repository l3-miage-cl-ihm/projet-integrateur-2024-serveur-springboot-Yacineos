package fr.uga.l3miage.integrator.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Schema(description = "Delivery representation for planner")
public class DeliveryPlannerResponseDTO {
    @Schema(description = "order of the delivery")
    private Set<String> orders;
    @Schema(description = "distance to cover from last delivery or from warehouse")
    private Double distanceToCover;
    @Schema(description = "The address of the delivery", example = "21 Rue des beaux temps|37600|Vancouver")
    private String address;

    @Schema(description = "Delivery GPS coordinates")
    private final List<Double> coordinates;


}
