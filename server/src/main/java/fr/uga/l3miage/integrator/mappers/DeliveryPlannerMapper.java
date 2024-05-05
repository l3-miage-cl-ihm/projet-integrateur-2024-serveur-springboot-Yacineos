package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.DeliveryPlannerResponseDTO;
import fr.uga.l3miage.integrator.responses.TourPlannerResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DeliveryPlannerMapper {
    DeliveryPlannerMapper INSTANCE = Mappers.getMapper(DeliveryPlannerMapper.class);


   // DeliveryPlannerResponseDTO toResponse(DeliveryEntity deliveryEntity);

    //DeliveryEntity  toEntity(DeliveryCreationRequest deliveryCreationRequest);

}
