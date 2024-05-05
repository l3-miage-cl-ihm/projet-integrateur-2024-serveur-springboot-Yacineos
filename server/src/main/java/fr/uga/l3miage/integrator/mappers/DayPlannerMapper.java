package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {TourPlannerMapper.class})
public interface DayPlannerMapper
{
    DayPlannerMapper INSTANCE = Mappers.getMapper(DayPlannerMapper.class);


    //DayResponseDTO  toResponse(DayEntity dayEntity);

    //DayEntity  toEntity(DayCreationRequest daycreationRequest);

}
