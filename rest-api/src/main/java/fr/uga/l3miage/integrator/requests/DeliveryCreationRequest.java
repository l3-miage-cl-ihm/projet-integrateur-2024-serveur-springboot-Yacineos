package fr.uga.l3miage.integrator.requests;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class DeliveryCreationRequest {

    private Set<String> orders;

    private double distanceToCover;
}
