package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.endpoints.DeliverymanEndpoints;
import fr.uga.l3miage.integrator.enums.DeliveryState;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import fr.uga.l3miage.integrator.services.DeliveryService;
import fr.uga.l3miage.integrator.services.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class DeliverymanControllers implements DeliverymanEndpoints {
    private final TourService tourService;
    private final DeliveryService deliveryService;
    @Override
    public TourDMResponseDTO getTour(String email) {
        return  tourService.getDeliveryTourOfTheDay(email) ;

    }
    @Override
    public void updateDeliveryState(DeliveryState deliveryState, String deliveryId,String tourId)  {
        deliveryService.updateDeliveryState(deliveryState,deliveryId,tourId);
    }
}
