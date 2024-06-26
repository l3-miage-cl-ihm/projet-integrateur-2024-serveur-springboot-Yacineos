package fr.uga.l3miage.integrator.responses;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Schema(description = "Delivery DM representation")
public class DeliveryDMResponseDTO {

    @Schema(description = "Delivery id ")
    private String deliveryId ;

    @Schema(description = "Orders ids list")
    private Set<String> orders;

    @Schema(description = "Customer  with format: 'firstName lastName' ", example="Joe LEROY")
    private String customer;

    @Schema(description = "Customer address with format: 'address|postalCode|city'",example = "21 Rue des beaux temps|37600|Vancouver")
    private String customerAddress;

    @Schema(description = "Delivery GPS coordinates")
    private List<Double> coordinates;


}
