package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.mappers.utils.DeliveryPlannerMapperUtils;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.responses.DeliveryPlannerResponseDTO;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(uses = {DeliveryPlannerMapperUtils.class})
@DecoratedWith(DeliveryPlannerMapperDecorator.class)
public interface DeliveryPlannerMapper {


    @Mapping(target = "reference",ignore = true)
    @Mapping(target = "state",ignore = true)
    @Mapping(target = "actualAssemblyTime",ignore = true)
    @Mapping(target = "actualDeliveryTime",ignore = true)
    @Mapping(target = "orders",ignore = true)
    DeliveryEntity  toEntity(DeliveryCreationRequest deliveryCreationRequest,String deliveryRef);


    @Mapping(source = "orders",target = "orders",qualifiedBy=DeliveryPlannerMapperUtils.GetOrdersIDs.class)
    @Mapping(source = "orders",target = "address",qualifiedBy = DeliveryPlannerMapperUtils.GetDeliveryAddress.class)
    DeliveryPlannerResponseDTO toResponse(DeliveryEntity deliveryEntity);


}
