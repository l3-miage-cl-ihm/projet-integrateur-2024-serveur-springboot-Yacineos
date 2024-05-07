package fr.uga.l3miage.integrator.responses;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data

@Schema(description = "Tour representation fot deliveryman")
public class TourDMResponseDTO {
    @Schema(description = "Deliveries list ")
    private Set<DeliveryDMResponseDTO> deliveries ;

    @Schema(description = "Deliverymen ids list")
    private Set<String> deliverymen;

    @Schema(description = "Truck id")
    private String truck;

    @Schema(description = "Tour reference")
    private String refTour;

    @Schema(description = "Day reference")
    private String refDay;

    @Schema(description = "Warehouse name ")
    private String warehouseName;

}