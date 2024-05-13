package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.enums.DeliveryState;
import fr.uga.l3miage.integrator.exceptions.rest.DayCreationRestException;
import fr.uga.l3miage.integrator.exceptions.rest.DayNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
public abstract class DeliveryPlannerMapperDecorator implements DeliveryPlannerMapper{
    @Autowired
    @Qualifier("delegate")
    private DeliveryPlannerMapper delegate;
    @Autowired
    private OrderRepository orderRepository;
    @Override
    public DeliveryEntity toEntity(DeliveryCreationRequest deliveryCreationRequest,String deliveryRef){
        DeliveryEntity deliveryEntity= delegate.toEntity(deliveryCreationRequest, deliveryRef);
        deliveryEntity.setReference(deliveryRef);
        deliveryEntity.setState(DeliveryState.PLANNED);
        Set<OrderEntity> orders= new HashSet<>();
        deliveryCreationRequest.getOrders()
                .forEach(deliveryId -> {
                    try {
                        OrderEntity order = orderRepository.findById(deliveryId).orElseThrow(()-> new InvalidInputValueException("No order was found with given id <"+deliveryId+">"));
                        orders.add(order);
                    } catch (InvalidInputValueException e) {
                        throw new DayCreationRestException(e.getMessage());
                    }
                });

        double lat= deliveryCreationRequest.getCoordinates().get(0);
        double lon=deliveryCreationRequest.getCoordinates().get(1);
        Coordinates coordinates= Coordinates.builder().lat(lat).lon(lon).build();
        deliveryEntity.setCoordinates(coordinates);
        deliveryEntity.setOrders(orders);
        return deliveryEntity;
    };

}
