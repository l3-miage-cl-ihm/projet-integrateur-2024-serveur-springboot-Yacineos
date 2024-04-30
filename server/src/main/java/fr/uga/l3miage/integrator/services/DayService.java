package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.DayComponent;
import fr.uga.l3miage.integrator.components.DeliveryComponent;
import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.exceptions.rest.DayCreationRestException;
import fr.uga.l3miage.integrator.exceptions.technical.DayAlreadyPlannedException;
import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.mappers.*;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class DayService {

    private final DayComponent dayComponent;
    private final TourComponent tourComponent;
    private final DeliveryComponent deliveryComponent;
    private final DayPlannerMapper dayPlannerMapper;
    private  final TourPlannerMapper tourPlannerMapper;
    private final  DeliveryPlannerMapper deliveryPlannerMapper;
    public void planDay(DayCreationRequest dayCreationRequest){
        try {
            //check wether the day is not already planned
           boolean isDayPlanned = dayComponent.isDayAlreadyPlanned(dayCreationRequest.getDate());
           if(isDayPlanned)
               throw new DayAlreadyPlannedException("Day is already planned !");

            //else check if inputs are not empty
            Set<TourCreationRequest> tours = dayCreationRequest.getTours();
            if(tours.isEmpty())
                throw  new InvalidInputValueException("Invalid input values, need 1 tour at least !");

            boolean anyInvalidTour= tours.stream().anyMatch(tour -> tour.getDeliveries().isEmpty() || (tour.getDeliverymen().size()!=2  && tour.getDeliverymen().size()!=1) || tour.getTruck().isEmpty() );
            if(anyInvalidTour)
                throw new InvalidInputValueException("Invalid inputs !, need 1 truck, 1 or 2 delivermen and some deliveries");

            //finally plan the day by adding to it its tours and to tours their deliveries
             DayEntity dayEntity = dayPlannerMapper.toEntity(dayCreationRequest);

             //add tours to day
            AtomicInteger tourIndex= new AtomicInteger(0);
            Set<TourEntity> dayTours = new HashSet<>();
            dayCreationRequest.getTours().forEach(tourCreationRequest -> {
                try {
                    TourEntity tourEntity = tourPlannerMapper.toEntity(tourCreationRequest,tourComponent.generateTourReference(dayCreationRequest.getDate(),tourIndex.get()));

                    //add deliveries to tour
                    AtomicInteger deliveryIndex= new AtomicInteger(0);
                    Set<DeliveryEntity> tourDeliveries= new HashSet<>();
                    tourCreationRequest.getDeliveries().forEach(deliveryCreationRequest -> {
                        DeliveryEntity deliveryEntity = deliveryPlannerMapper.toEntity(deliveryCreationRequest,deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),deliveryIndex.get()));
                        //save delivery and add it to tourDeliveries
                        deliveryComponent.saveDelivery(deliveryEntity);
                        tourDeliveries.add(deliveryEntity);
                        deliveryIndex.getAndIncrement();
                    });


                    tourEntity.setDeliveries(tourDeliveries);
                    //save tour and add it to dayTours
                    tourComponent.saveTour(tourEntity);
                    dayTours.add(tourEntity);
                    tourIndex.getAndIncrement();

                } catch (InvalidInputValueException e) {
                    throw new DayCreationRestException(e.getMessage());
                }

            });

            //add tours into day and save it.
            dayEntity.setTours(dayTours);
            dayComponent.planDay(dayEntity);


        }catch (DayAlreadyPlannedException | InvalidInputValueException e){
            throw new DayCreationRestException(e.getMessage());
        }


    }
    public SetUpBundleResponse getSetUpBundle(){
        return null ;
    }
    public DayResponseDTO getDay(LocalDate date){


            DayEntity day = dayComponent.getDay(date);
            return null ;//dayMapper.toResponse(day);

    }
}
