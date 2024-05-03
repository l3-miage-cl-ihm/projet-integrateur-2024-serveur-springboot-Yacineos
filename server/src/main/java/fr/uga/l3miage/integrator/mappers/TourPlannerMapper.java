package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.dataTypes.Address;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;


@Mapper(uses = {DeliveryPlannerMapper.class})
@DecoratedWith(TourPlannerMapperDecorator.class)
public interface TourPlannerMapper {

    @Mapping(source = "truck",target = "truck",qualifiedByName = "getTruckID")
    @Mapping(source = "reference", target = "refTour")
    @Mapping(source= "deliverymen",target = "deliverymen", qualifiedByName = "getDeliveryMenIDs")
    TourPlannerResponseDTO toResponse(TourEntity tourEntity);
  
  
    @Mapping(target = "deliverymen", ignore=true)
    @Mapping(target = "deliveries", ignore=true)
    @Mapping(target = "reference", ignore=true)
    @Mapping(target = "state", ignore=true)
    @Mapping(target = "letter", ignore=true)
    @Mapping(target = "actualAssemblyTime", ignore=true)
    @Mapping(target = "truck", ignore=true)
    TourEntity  toEntity(TourCreationRequest tourCreationRequest,String tourRef) throws InvalidInputValueException;


    @Named("getTruckID")
    default String getTruckID (TruckEntity truckEntity){
        return truckEntity.getImmatriculation();
    }
    @Named("getDeliveryMenIDs")
    default Set<String> getDeliveryMenIDs(Set<EmployeeEntity> deliverymen){
        return deliverymen.stream().map(EmployeeEntity::getTrigram).collect(Collectors.toSet());
    }
}
