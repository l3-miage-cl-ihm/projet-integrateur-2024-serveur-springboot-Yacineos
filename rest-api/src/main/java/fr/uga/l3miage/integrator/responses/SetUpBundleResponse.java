package fr.uga.l3miage.integrator.responses;

import fr.uga.l3miage.integrator.dataTypes.MultipleOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "Set up Bundle representation")
public class SetUpBundleResponse {
    @Schema(description = "Set of MultipleOrder containing a Set of orders ids with the same address ")
    private Set<String> multipleOrders;

    @Schema(description = "Deliverymen ids list")
    private Set<String> deliverymen;

    @Schema(description = "Trucks ids")
    private Set<String> truck;
}
