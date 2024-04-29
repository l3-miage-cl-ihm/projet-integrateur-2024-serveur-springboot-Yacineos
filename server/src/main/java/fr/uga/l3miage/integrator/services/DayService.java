package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.DayComponent;
import fr.uga.l3miage.integrator.exceptions.rest.DayCreationRestException;
import fr.uga.l3miage.integrator.exceptions.technical.DayAlreadyPlannedException;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.mappers.DayPlannerMapper;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.repositories.DayRepository;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DayService {

    private final DayComponent dayComponent;
    private final DayPlannerMapper dayMapper;
    public void planDay(DayCreationRequest dayCreationRequest){
        try {
            //check wether the day is not already planned
           boolean isDayPlanned = dayComponent.isDayAlreadyPlanned(dayCreationRequest.getDate());
           if(isDayPlanned)
               throw new DayAlreadyPlannedException("Day is already planned !");

            //else check if inputs are valid
            Set<TourCreationRequest> tours = dayCreationRequest.getTours();
            if(tours.isEmpty())
                throw  new InvalidInputValueException("Invalid input values, need 1 tour at least !");

            boolean anyInvalidTour= tours.stream().anyMatch(tour -> tour.getDeliveries().isEmpty() || (tour.getDeliverymen().size()!=2  && tour.getDeliverymen().size()!=1) || tour.getTruck().isEmpty() );
            if(anyInvalidTour)
                throw new InvalidInputValueException("Invalid inputs !, need 1 truck, 1 or 2 delivermen and some deliveries");

            //finally plan the day
            dayComponent.planDay(dayMapper.toEntity(dayCreationRequest));


        }catch (DayAlreadyPlannedException | InvalidInputValueException e){
            throw new DayCreationRestException(e.getMessage());
        }


    }
    public SetUpBundleResponse getSetUpBundle(){
        return null ;
    }
    public DayResponseDTO getDay(LocalDate date){


            DayEntity day = dayComponent.getDay(date);
            return dayMapper.toResponse(day);

    }
}
