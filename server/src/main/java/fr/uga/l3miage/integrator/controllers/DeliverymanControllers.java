package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.endpoints.DeliverymanEndpoints;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.DeliveryDMResponseDTO;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import fr.uga.l3miage.integrator.services.TourService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class DeliverymanControllers implements DeliverymanEndpoints {
    private final TourService tourService;
    @Override
    public TourDMResponseDTO getTour(String email) {
        return  tourService.getDeliveryTourOfTheDay(email) ;

    }
}
