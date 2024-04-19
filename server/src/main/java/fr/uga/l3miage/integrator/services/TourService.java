package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.exceptions.rest.EntityNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourComponent tourComponent;

    public TourDMResponseDTO getDeliveryTourOfTheDay(String email){

       try{
           TourEntity tour = tourComponent.getDeliveryTourOfTheDay(email);
           //return TourDMMapper.toResponse(tour)
            return null;
       }catch( DayNotFoundException | TourNotFoundException e){
           throw new EntityNotFoundRestException(e.getMessage());

       }

    }
}
