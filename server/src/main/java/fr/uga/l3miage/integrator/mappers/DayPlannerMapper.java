package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
@DecoratedWith(DayPlannerMapperDecorator.class)
public interface DayPlannerMapper {
    DayPlannerMapper INSTANCE = Mappers.getMapper(DayPlannerMapper.class);

    //DayResponseDTO  toResponse(DayEntity dayEntity);

    @Mapping(target = "reference",ignore = true)
    @Mapping(target = "state",ignore = true)
    @Mapping(target = "planner",ignore = true)
    @Mapping(target = "tours", ignore=true)
    DayEntity  toEntity(DayCreationRequest dayCreationRequest);

}