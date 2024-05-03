package fr.uga.l3miage.integrator.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
@Schema(description = "Delivery creation")
public class DeliveryCreationRequest {

    @Schema(description = "Delivery associated orders ")
    private final  Set<String> orders;

    @Schema(description = "distance to cover ", example = "20.3")
    private final double distanceToCover;
}
