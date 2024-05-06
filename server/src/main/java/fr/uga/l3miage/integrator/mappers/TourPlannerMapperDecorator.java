package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.enums.TourState;
import fr.uga.l3miage.integrator.exceptions.rest.DayCreationRestException;
import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.models.TruckEntity;
import fr.uga.l3miage.integrator.repositories.EmployeeRepository;
import fr.uga.l3miage.integrator.repositories.TruckRepository;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;
import java.util.Set;

public abstract class TourPlannerMapperDecorator implements TourPlannerMapper {

    @Autowired
    @Qualifier("delegate")
    private TourPlannerMapper delegate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private TruckRepository truckRepository;

    @Override
    public TourEntity toEntity(TourCreationRequest tourCreationRequest, String tourRef) throws InvalidInputValueException {
        TourEntity tourEntity= delegate.toEntity(tourCreationRequest,tourRef);
        tourEntity.setReference(tourRef);
        tourEntity.setState(TourState.PLANNED);

        TruckEntity truck=truckRepository.findById(tourCreationRequest.getTruck()).orElseThrow(()-> new InvalidInputValueException("Truck <"+tourCreationRequest.getTruck()+"> not found !"));
        tourEntity.setTruck(truck);

        Set<EmployeeEntity> deliverymen= new HashSet<>();
        tourCreationRequest.getDeliverymen()
                .forEach(deliverymanTrigram -> {
                    try {
                        EmployeeEntity deliveryman= employeeRepository
                                .findById(deliverymanTrigram).orElseThrow(()-> new InvalidInputValueException("No deliveryman was found with given trigram <"+deliverymanTrigram+">"));
                        deliverymen.add(deliveryman);
                    } catch (InvalidInputValueException e) {
                        throw new DayCreationRestException(e.getMessage());
                    }

                });

        tourEntity.setDeliverymen(deliverymen);
        return tourEntity;
    }

}
