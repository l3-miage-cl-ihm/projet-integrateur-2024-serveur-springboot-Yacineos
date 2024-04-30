package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
@DecoratedWith(TourPlannerMapperDecorator.class)
public interface TourPlannerMapper {

    TourPlannerMapper INSTANCE = Mappers.getMapper(TourPlannerMapper.class);


    //TourPlannerResponseDTO toResponse(TourEntity tourEntity);

    @Mapping(target = "deliverymen", ignore=true)
    @Mapping(target = "deliveries", ignore=true)
    @Mapping(target = "reference", ignore=true)
    @Mapping(target = "state", ignore=true)
    @Mapping(target = "letter", ignore=true)
    @Mapping(target = "actualAssemblyTime", ignore=true)
    @Mapping(target = "truck", ignore=true)
    TourEntity  toEntity(TourCreationRequest tourCreationRequest,String tourRef) throws InvalidInputValueException;



}
