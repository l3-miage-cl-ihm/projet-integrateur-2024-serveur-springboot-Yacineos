package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
@DecoratedWith(DeliveryPlannerMapperDecorator.class)
public interface DeliveryPlannerMapper {

    //DeliveryPlannerResponseDTO toResponse(DeliveryEntity deliveryEntity);
    @Mapping(target = "reference",ignore = true)
    @Mapping(target = "state",ignore = true)
    @Mapping(target = "actualAssemblyTime",ignore = true)
    @Mapping(target = "actualDeliveryTime",ignore = true)
    @Mapping(target = "orders",ignore = true)
    DeliveryEntity  toEntity(DeliveryCreationRequest deliveryCreationRequest,String deliveryRef);

}
