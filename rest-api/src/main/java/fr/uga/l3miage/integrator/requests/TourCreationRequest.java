package fr.uga.l3miage.integrator.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
@Schema(description = "Tour creation")
public class TourCreationRequest {

    @Schema(description = "Tour deliveries")
    private final List<DeliveryCreationRequest> deliveries;

    @Schema(description = "Tour deliverymen ")
    private final Set<String> deliverymen;
    @Schema(description = "Tour associated truck ",example = "XY-435-RT")
    private final String truck;
    @Schema(description = "Distance to cover", example = "12")
    private final double distanceToCover;

}
