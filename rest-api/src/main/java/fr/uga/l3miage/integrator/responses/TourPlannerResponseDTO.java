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
    @Schema(description = "Truck id", example = "XY-423-TR")
    private String truck;
    @Schema(description ="Total amount of distance to cover for the Tour",example = "5.0")
    private Double distanceToCover;
    @Schema(description ="tour reference",example = "t135G-A")
    private String refTour;

    @Schema(description ="Warehouse coordinates")
    private List<Double> coordinates;


}
