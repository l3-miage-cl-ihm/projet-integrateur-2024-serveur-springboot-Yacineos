package fr.uga.l3miage.integrator.responses;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data

@Schema(description = "Tour representation fot deliveryman")
public class TourDMResponseDTO {
    @Schema(description = "Deliveries list ")
    private List<DeliveryDMResponseDTO> deliveries ;

    @Schema(description = "Deliverymen ids list")
    private Set<String> deliverymen;

    @Schema(description = "Truck id", example = "XY-423-TR")
    private String truck;

    @Schema(description = "Tour reference",example = "t135G-B")
    private String refTour;

    @Schema(description = "Day reference",example = "J135G")
    private String refDay;

    @Schema(description = "Warehouse name ",example = "Grenis")
    private String warehouseName;

}