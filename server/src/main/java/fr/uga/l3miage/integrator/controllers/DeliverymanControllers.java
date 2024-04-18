package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.endpoints.DeliverymanEndpoints;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DeliverymanControllers implements DeliverymanEndpoints {
    @Override
    public TourDMResponseDTO getTour(String email) {
        return null;
    }
}
