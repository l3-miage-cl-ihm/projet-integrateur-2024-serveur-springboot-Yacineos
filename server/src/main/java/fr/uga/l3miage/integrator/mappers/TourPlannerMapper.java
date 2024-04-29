package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.TourPlannerResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(uses = {DeliveryPlannerMapper.class})
public interface TourPlannerMapper {

    TourPlannerMapper INSTANCE = Mappers.getMapper(TourPlannerMapper.class);

    @Mapping(source = "reference", target = "refTour")
    TourPlannerResponseDTO toResponse(TourEntity tourEntity);

    TourEntity  toEntity(TourCreationRequest tourCreationRequest);

}
