package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.*;
import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.enums.OrderState;
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
@Service
@RequiredArgsConstructor
public class DayService {

    private final DayComponent dayComponent;
    private final TourComponent tourComponent;
    private final DeliveryComponent deliveryComponent;
    private final DayPlannerMapper dayPlannerMapper;
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

             //here swtich orders state into PLANNED so as to not get the same orders the next day
            dayEntity.getTours().forEach(tour ->
                    tour.getDeliveries().forEach(deliveryEntity ->
                            deliveryEntity.getOrders().forEach(orderEntity -> {
                                orderEntity.setState(OrderState.PLANNED);
                                orderComponent.saveOrder(orderEntity);
                            }))   );
            dayComponent.planDay(dayEntity);


        }catch (DayAlreadyPlannedException | InvalidInputValueException e){
            throw new DayCreationRestException(e.getMessage());
        }


    }
    public void editDay(DayCreationRequest dayEditRequest, String dayId){
        try {
            //check wether the day to edit exists in the db
            DayEntity day = dayComponent.getDayById(dayId);

            //Switch orders state into OPENNED so as to consider the new dayEditDay with the right orders.
            day.getTours().forEach(tour ->
                    tour.getDeliveries().forEach(deliveryEntity ->
                            deliveryEntity.getOrders().forEach(orderEntity -> {
                                orderEntity.setState(OrderState.OPENED);
                                orderComponent.saveOrder(orderEntity);
                            }))   );

            //clean existing day by removing its relations
            day.getTours().forEach(tour -> tour.getDeliveries().forEach(deliveryEntity -> deliveryComponent.deleteDelivery(deliveryEntity.getReference())));
            day.getTours().forEach(tour -> tourComponent.deleteTour(tour.getReference()));
            dayComponent.deleteDay(dayId);
            //Then plan it
            this.planDay(dayEditRequest);

        }
         catch (DayNotFoundException e) {
            throw new DayNotFoundRestException(e.getMessage());
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
            return dayPlannerMapper.toResponse(day);

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
