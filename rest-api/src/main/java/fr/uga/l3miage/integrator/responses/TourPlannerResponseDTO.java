package fr.uga.l3miage.integrator.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
@Schema(description = "Tour representation for planner")
public class TourPlannerResponseDTO {
    @Schema(description = "Deliveries list ")
    private List<DeliveryPlannerResponseDTO> deliveries ;
    @Schema(description = "list of deliveryMan id")
    private Set<String> deliverymen;
    @Schema(description = "Truck id")
    private String truck;
    @Schema(description ="Total amount of distance to cover for the Tour")
    private Double distanceToCover;

    @Schema(description ="tour reference")
    private String refTour;

}
