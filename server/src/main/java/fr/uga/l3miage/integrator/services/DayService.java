package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.DayComponent;
import fr.uga.l3miage.integrator.components.EmployeeComponent;
import fr.uga.l3miage.integrator.components.OrderComponent;
import fr.uga.l3miage.integrator.components.TruckComponent;
import fr.uga.l3miage.integrator.components.DeliveryComponent;
import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.exceptions.rest.DayCreationRestException;
import fr.uga.l3miage.integrator.exceptions.rest.EntityNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.technical.DayAlreadyPlannedException;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.mappers.*;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.DeliveryPlannerResponseDTO;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import fr.uga.l3miage.integrator.responses.TourPlannerResponseDTO;
import fr.uga.l3miage.integrator.responses.datatypes.MultipleOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
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
    private final TruckComponent truckComponent;
    private final EmployeeComponent employeeComponent;
    private final OrderComponent orderComponent;
    public void planDay(DayCreationRequest dayCreationRequest){
        try {
            //check wether the day is not already planned
           boolean isDayPlanned = dayComponent.isDayAlreadyPlanned(dayCreationRequest.getDate());
           if(isDayPlanned)
               throw new DayAlreadyPlannedException("Day is already planned !");

            //else check if inputs are not empty
            List<TourCreationRequest> tours = dayCreationRequest.getTours();
            if(tours.isEmpty())
                throw  new InvalidInputValueException("Invalid input values, need 1 tour at least !");

            boolean anyInvalidTour= tours.stream().anyMatch(tour -> tour.getDeliveries().isEmpty() || (tour.getDeliverymen().size()!=2  && tour.getDeliverymen().size()!=1) || tour.getTruck().isEmpty() );
            if(anyInvalidTour)
                throw new InvalidInputValueException("Invalid inputs, need 1 truck, 1 or 2 deliverymen and at least one delivery !");

            //finally plan the day by adding to it its tours and to tours their deliveries
             DayEntity dayEntity = dayPlannerMapper.toEntity(dayCreationRequest);

             //add tours to day
            AtomicInteger tourIndex= new AtomicInteger(0);
            List<TourEntity> dayTours = new ArrayList<>();
            for(TourCreationRequest tourCreationRequest : dayCreationRequest.getTours() ) {
                try {
                    String refTour=tourComponent.generateTourReference(dayCreationRequest.getDate(),tourIndex.get());
                    String tourLetter=Character.toString(refTour.charAt(refTour.length()-1));
                    TourEntity tourEntity = tourPlannerMapper.toEntity(tourCreationRequest,refTour);

                    //add deliveries to tour
                    AtomicInteger deliveryIndex= new AtomicInteger(1);
                    List<DeliveryEntity> tourDeliveries= new ArrayList<>();
                    for(DeliveryCreationRequest deliveryCreationRequest: tourCreationRequest.getDeliveries() ) {
                        DeliveryEntity deliveryEntity = deliveryPlannerMapper.toEntity(deliveryCreationRequest,deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),deliveryIndex.get(),tourLetter));
                        //save delivery and add it to tourDeliveries
                        deliveryComponent.saveDelivery(deliveryEntity);
                        tourDeliveries.add(deliveryEntity);
                        deliveryIndex.getAndIncrement();
                    }

                    tourEntity.setLetter(tourLetter);
                    tourEntity.setDeliveries(tourDeliveries);
                    //save tour and add it to dayTours
                    tourComponent.saveTour(tourEntity);
                    dayTours.add(tourEntity);
                    tourIndex.getAndIncrement();

                } catch (InvalidInputValueException e) {
                    throw new DayCreationRestException(e.getMessage());
                }

            }
            //add tours into day and save it.
            dayEntity.setTours(dayTours);
            dayComponent.planDay(dayEntity);


        }catch (DayAlreadyPlannedException | InvalidInputValueException e){
            throw new DayCreationRestException(e.getMessage());
        }


    }
    public SetUpBundleResponse getSetUpBundle(){

        SetUpBundleResponse setUpBundleResponse = new SetUpBundleResponse();
        LinkedHashSet<MultipleOrder> multipleOrder = new LinkedHashSet<>();
        multipleOrder = orderComponent.createMultipleOrders();
//        Set<String> multipleOrderSet = new HashSet<>();
//        for (MultipleOrder multipleOrder1 : multipleOrder){
//            multipleOrderSet.add(multipleOrder1.toString());
//        }

        Set<String> immatriculationTrucks = truckComponent.getAllTrucksImmatriculation();
        Set<String> idLivreurs = employeeComponent.getAllDeliveryMenID();

        setUpBundleResponse.setMultipleOrders(multipleOrder);
        setUpBundleResponse.setDeliverymen(idLivreurs);
        setUpBundleResponse.setTrucks(immatriculationTrucks);
        return setUpBundleResponse ;
    }
    public DayResponseDTO getDay(LocalDate date){
        try {
            DayEntity day = dayComponent.getDay(date);

            DayResponseDTO dayResponseDTO = dayPlannerMapper.toResponse(day);

            List<TourPlannerResponseDTO> toursInDay = new ArrayList<>();
            for (TourEntity tourEntity : day.getTours()) {
                TourPlannerResponseDTO tourPlannerResponseDTO = tourPlannerMapper.toResponse(tourEntity);
                List<DeliveryPlannerResponseDTO> deliveriesInTour = new ArrayList<>();
                tourEntity.getDeliveries().forEach(deliveryEntity -> {
                    DeliveryPlannerResponseDTO deliveryPlannerResponseDTO = deliveryPlannerMapper.toResponse(deliveryEntity);
                    deliveriesInTour.add(deliveryPlannerResponseDTO);
                });

                tourPlannerResponseDTO.setDeliveries(deliveriesInTour);
                toursInDay.add(tourPlannerResponseDTO);
            }

            dayResponseDTO.setTours(toursInDay);
            return dayResponseDTO;

        } catch (DayNotFoundException e) {
            throw new EntityNotFoundRestException(e.getMessage());
        }

    }




}
