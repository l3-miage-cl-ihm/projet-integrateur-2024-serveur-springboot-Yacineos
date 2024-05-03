package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.models.TruckEntity;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.TourPlannerResponseDTO;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = {DeliveryPlannerMapper.class})
@DecoratedWith(TourPlannerMapperDecorator.class)
public interface TourPlannerMapper {


    @Mapping(source = "truck",target = "truck",qualifiedByName = "getTruckID")
    @Mapping(source = "reference", target = "refTour")
    TourPlannerResponseDTO toResponse(TourEntity tourEntity);

    @Named("getTruckID")
    default String getTruckID (TruckEntity truckEntity){
        return truckEntity.getImmatriculation();
    }

    @Mapping(target = "deliverymen", ignore=true)
    @Mapping(target = "deliveries", ignore=true)
    @Mapping(target = "reference", ignore=true)
    @Mapping(target = "state", ignore=true)
    @Mapping(target = "letter", ignore=true)
    @Mapping(target = "actualAssemblyTime", ignore=true)
    @Mapping(target = "truck", ignore=true)
    TourEntity  toEntity(TourCreationRequest tourCreationRequest,String tourRef) throws InvalidInputValueException;



}
