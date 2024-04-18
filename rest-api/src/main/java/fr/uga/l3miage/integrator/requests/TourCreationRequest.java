package fr.uga.l3miage.integrator.requests;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class TourCreationRequest {

    private Set<DeliveryCreationRequest> deliveries;
    private Set<String> deliverymen;
    private String truck;
    private double distanceToCover;

}
