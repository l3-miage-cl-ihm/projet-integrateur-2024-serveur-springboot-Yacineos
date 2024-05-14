package fr.uga.l3miage.integrator.responses;

import fr.uga.l3miage.integrator.responses.datatypes.MultipleOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Data
@Schema(description = "Set up Bundle representation")
public class SetUpBundleResponse {
    @Schema(description = "Set of MultipleOrder containing a Set of orders ids with the same address ")
    private LinkedHashSet<MultipleOrder> multipleOrders;

    @Schema(description = "Deliverymen ids list")
    private Set<String> deliverymen;

    @Schema(description = "Trucks ids")
    private Set<String> trucks;

    @Schema(description = "Warehouse coordinates")
    private List<Double> coordinates;
}
