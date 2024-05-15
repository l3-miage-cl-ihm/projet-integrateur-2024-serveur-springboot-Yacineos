package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.components.DeliveryComponent;
import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.exceptions.rest.DayCreationRestException;
import fr.uga.l3miage.integrator.exceptions.rest.EntityNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.repositories.EmployeeRepository;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.DeliveryPlannerResponseDTO;
import fr.uga.l3miage.integrator.responses.TourPlannerResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;


public abstract class DayPlannerMapperDecorator implements DayPlannerMapper {
    //other mappers injection
    @Autowired
    @Qualifier("delegate")
    private DayPlannerMapper delegate;
    @Autowired
    private TourPlannerMapper tourPlannerMapper;
    @Autowired
    private DeliveryPlannerMapper deliveryPlannerMapper;
    @Autowired
    private TourComponent tourComponent;
    @Autowired
    private DeliveryComponent deliveryComponent;


    @Autowired
    private EmployeeRepository employeeRepository;
    @Override
    public DayEntity toEntity(DayCreationRequest dayCreationRequest) {
        DayEntity dayEntity = delegate.toEntity(dayCreationRequest);
        dayEntity.setState(DayState.PLANNED);
        dayEntity.setPlanner(employeeRepository.findById("STR").orElseThrow(()->new DayCreationRestException("No planner was found with given trigram <STR> !")));//We supposed to work with only one warehouse which is "GRENIS".

        //add tours to day
        int tourIndex= 0;
        List<TourEntity> dayTours = new ArrayList<>();
        for(TourCreationRequest tourCreationRequest : dayCreationRequest.getTours() ) {
            try {
                String refTour=tourComponent.generateTourReference(dayCreationRequest.getDate(),tourIndex);
                String tourLetter=Character.toString(refTour.charAt(refTour.length()-1));
                TourEntity tourEntity = tourPlannerMapper.toEntity(tourCreationRequest,refTour);

                //add deliveries to tour
                int deliveryIndex= 1;
                List<DeliveryEntity> tourDeliveries= new ArrayList<>();
                for(DeliveryCreationRequest deliveryCreationRequest: tourCreationRequest.getDeliveries() ) {
                    DeliveryEntity deliveryEntity = deliveryPlannerMapper.toEntity(deliveryCreationRequest,deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),deliveryIndex,tourLetter));
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

        return dayEntity;
    }

    @Override
   public  DayResponseDTO toResponse(DayEntity dayEntity){
        DayResponseDTO dayResponseDTO = delegate.toResponse(dayEntity);
        List<TourPlannerResponseDTO> toursInDay = new ArrayList<>();
        for (TourEntity tourEntity : dayEntity.getTours()) {
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

    }




}
