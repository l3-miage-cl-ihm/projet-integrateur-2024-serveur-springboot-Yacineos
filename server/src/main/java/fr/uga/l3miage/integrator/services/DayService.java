package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.*;
import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.exceptions.rest.*;
import fr.uga.l3miage.integrator.exceptions.technical.*;
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
    private final WarehouseComponent warehouseComponent;
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

            boolean anyInvalidTour= tours.stream().anyMatch(tour -> tour.getDeliveries().isEmpty() || (tour.getDeliverymen().size()!=2  && tour.getDeliverymen().size()!=1) || tour.getTruck().isEmpty() )  ;
            if(anyInvalidTour)
                throw new InvalidInputValueException("Invalid inputs, need 1 truck, 1 or 2 deliverymen , at least one delivery and also make sure you didn't plan the same order in other tours !");

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
    public void editDay(DayCreationRequest dayEditRequest, String dayId){
        try{
           //check wether the day to edit exist in the db
            DayEntity day = dayComponent.getDayById(dayId);

            //check wether the new given date of new day is different of the existent day
            if( ! day.getDate().equals(dayEditRequest.getDate()))
                throw new DayAlreadyPlannedException("Cannot update to the new given date, please give the same date as the one you planned  !");

            // At this level we can check input validity and then edit the requested day
            List<TourCreationRequest> tours = dayEditRequest.getTours();
            if(tours.isEmpty())
                throw  new InvalidInputValueException("Invalid input values, need 1 tour at least !");

            boolean anyInvalidTour= tours.stream().anyMatch(tour -> tour.getDeliveries().isEmpty() || (tour.getDeliverymen().size()!=2  && tour.getDeliverymen().size()!=1) || tour.getTruck().isEmpty() );
            if(anyInvalidTour)
                throw new InvalidInputValueException("Invalid inputs, need 1 truck, 1 or 2 deliverymen and at least one delivery !");


            //finally edit the day by adding to it its tours and to tours their deliveries
            DayEntity dayEntity = dayPlannerMapper.toEntity(dayEditRequest);

            //add tours to day
            int tourIndex= 0;
            List<TourEntity> dayTours = new ArrayList<>();
            for(TourCreationRequest tourCreationRequest : dayEditRequest.getTours() ) {
                try {
                    String refTour=tourComponent.generateTourReference(dayEditRequest.getDate(),tourIndex);
                    String tourLetter=Character.toString(refTour.charAt(refTour.length()-1));
                    TourEntity tourEntity = tourPlannerMapper.toEntity(tourCreationRequest,refTour);

                    //add deliveries to tour
                    int deliveryIndex= 1;
                    List<DeliveryEntity> tourDeliveries= new ArrayList<>();
                    for(DeliveryCreationRequest deliveryCreationRequest: tourCreationRequest.getDeliveries() ) {
                        DeliveryEntity deliveryEntity = deliveryPlannerMapper.toEntity(deliveryCreationRequest,deliveryComponent.generateDeliveryReference(dayEditRequest.getDate(),deliveryIndex,tourLetter));
                        //save delivery and add it to tourDeliveries
                        deliveryComponent.saveDelivery(deliveryEntity);
                        tourDeliveries.add(deliveryEntity);
                        deliveryIndex++;
                    }

                    tourEntity.setLetter(tourLetter);
                    tourEntity.setDeliveries(tourDeliveries);
                    //save tour and add it to dayTours
                    tourComponent.saveTour(tourEntity);
                    dayTours.add(tourEntity);
                    tourIndex++;

                } catch (InvalidInputValueException e) {
                    throw new DayCreationRestException(e.getMessage());
                }

            }
            //add tours into day and save it.
            dayEntity.setTours(dayTours);
            day.setTours(dayEntity.getTours());
            day.setPlanner(dayEntity.getPlanner());
            dayComponent.planDay(day);

        }catch (DayNotFoundException e) {
            throw new DayNotFoundRestException(e.getMessage());
        } catch (InvalidInputValueException | DayAlreadyPlannedException e) {
            throw new EditDayRestException(e.getMessage());
        }
    }
    public SetUpBundleResponse getSetUpBundle(String idWarehouse){

        try {

            SetUpBundleResponse setUpBundleResponse = new SetUpBundleResponse();
            LinkedHashSet<MultipleOrder> multipleOrder = orderComponent.createMultipleOrders();
            Set<String> immatriculationTrucks = warehouseComponent.getAllTrucks(idWarehouse);
            Set<String> idLivreurs = employeeComponent.getAllDeliveryMenID(idWarehouse);
            Coordinates warehouseCoordinates = warehouseComponent.getWarehouseCoordinates(idWarehouse);
            setUpBundleResponse.setMultipleOrders(multipleOrder);
            setUpBundleResponse.setDeliverymen(idLivreurs);
            setUpBundleResponse.setTrucks(immatriculationTrucks);
            setUpBundleResponse.setCoordinates(List.of(warehouseCoordinates.getLat(), warehouseCoordinates.getLon()));
            return setUpBundleResponse;
        }catch (WarehouseNotFoundException e){
            throw new WarehouseNotFoundRestException(e.getMessage());
        }
    }
    public DayResponseDTO getDay(LocalDate date){
        try {
            DayEntity day = dayComponent.getDay(date);
            DayResponseDTO dayResponseDTO = dayPlannerMapper.toResponse(day);
            List<TourPlannerResponseDTO> toursInDay = new ArrayList<>();
            for (TourEntity tourEntity : day.getTours()) {
                TourPlannerResponseDTO tourPlannerResponseDTO = tourPlannerMapper.toResponse(tourEntity);
                List<DeliveryPlannerResponseDTO> deliveriesInTour = new ArrayList<>();
                for(DeliveryEntity deliveryEntity : tourEntity.getDeliveries()) {
                    DeliveryPlannerResponseDTO deliveryPlannerResponseDTO = deliveryPlannerMapper.toResponse(deliveryEntity);
                    deliveriesInTour.add(deliveryPlannerResponseDTO);
                };

                tourPlannerResponseDTO.setDeliveries(deliveriesInTour);
                toursInDay.add(tourPlannerResponseDTO);
            }

            dayResponseDTO.setTours(toursInDay);
            return dayResponseDTO;

        } catch (DayNotFoundException e) {
            throw new EntityNotFoundRestException(e.getMessage());
        }

    }

    public DayEntity updateDayState(String dayId, DayState newDayState){
        try{
            return dayComponent.updateDayState(dayId,newDayState);
        }catch (UpdateDayStateException e) {
            throw new UpdateDayStateRestException(e.getMessage(),e.getDayState());
        } catch (DayNotFoundException e) {
            throw new DayNotFoundRestException(e.getMessage());
        }
    }




}
