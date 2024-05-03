package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.exceptions.rest.DayNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.rest.EntityNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.rest.TourNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.mappers.TourDMMapper;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TourService {

    private final TourComponent tourComponent;
    private final TourDMMapper tourDMMapper;

    public TourDMResponseDTO getDeliveryTourOfTheDay(String email){

       try{
           TourEntity tour = tourComponent.getTourOfTheDay(email);
           return tourDMMapper.toResponse(tour);

       }catch( DayNotFoundException e){
           throw new DayNotFoundRestException(e.getMessage());

       }catch (TourNotFoundException e){
           throw new TourNotFoundRestException(e.getMessage());
       }

    }
}
