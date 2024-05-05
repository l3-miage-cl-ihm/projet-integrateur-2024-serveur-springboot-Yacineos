package fr.uga.l3miage.integrator.responses;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "contains the order's ids having the same adresses")
public class MultipleOrderDTO {

    @Schema(description = "a set of order's ids")
    Set<String> references;

    @Schema(description = "the address where the city, the postal code and the street are separated by | ")
    String address;
}
