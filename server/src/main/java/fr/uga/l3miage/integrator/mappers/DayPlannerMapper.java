package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.mappers.utils.DayPlannerMapperUtils;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {TourPlannerMapper.class,DeliveryPlannerMapper.class,DayPlannerMapperUtils.class})
@DecoratedWith(DayPlannerMapperDecorator.class)
public interface DayPlannerMapper {

    @Mapping(target = "state",ignore = true)
    @Mapping(target = "planner",ignore = true)
    @Mapping(target = "tours", ignore=true)
    @Mapping(source = "date", target = "reference",qualifiedBy = DayPlannerMapperUtils.GenerateDayReference.class)
    DayEntity  toEntity(DayCreationRequest dayCreationRequest);

    @Mapping(target = "tours", ignore=true)
    DayResponseDTO toResponse(DayEntity dayEntity);



}