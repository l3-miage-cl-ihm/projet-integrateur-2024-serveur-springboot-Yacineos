package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.DeliveryPlannerResponseDTO;
import fr.uga.l3miage.integrator.responses.TourPlannerResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface DeliveryPlannerMapper {
    DeliveryPlannerMapper INSTANCE = Mappers.getMapper(DeliveryPlannerMapper.class);

    @Mapping(source = "orders",target = "orders",qualifiedByName="getOrdersIDs")
    DeliveryPlannerResponseDTO toResponse(DeliveryEntity deliveryEntity);

    //DeliveryEntity  toEntity(DeliveryCreationRequest deliveryCreationRequest);


    @Named("getOrdersIDs")
    default Set<String> getOrdersIDs(Set<OrderEntity> orders){
        return   orders.stream().map(OrderEntity::getReference).collect(Collectors.toSet());

    }
}
