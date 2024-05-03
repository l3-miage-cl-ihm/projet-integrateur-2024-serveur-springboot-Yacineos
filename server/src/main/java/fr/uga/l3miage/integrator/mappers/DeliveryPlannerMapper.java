package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
@DecoratedWith(DeliveryPlannerMapperDecorator.class)
public interface DeliveryPlannerMapper {
  
    @Mapping(source = "orders",target = "orders",qualifiedByName="getOrdersIDs")
    DeliveryPlannerResponseDTO toResponse(DeliveryEntity deliveryEntity);

  
    @Named("getOrdersIDs")
    default Set<String> getOrdersIDs(Set<OrderEntity> orders) {
        return   orders.stream().map(OrderEntity::getReference).collect(Collectors.toSet());
    }

   
    @Mapping(target = "reference",ignore = true)
    @Mapping(target = "state",ignore = true)
    @Mapping(target = "actualAssemblyTime",ignore = true)
    @Mapping(target = "actualDeliveryTime",ignore = true)
    @Mapping(target = "orders",ignore = true)
    DeliveryEntity  toEntity(DeliveryCreationRequest deliveryCreationRequest,String deliveryRef);

}
