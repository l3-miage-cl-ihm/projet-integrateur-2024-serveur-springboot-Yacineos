package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.dataTypes.Address;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.TourPlannerResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;


@Mapper(uses = {DeliveryPlannerMapper.class})
public interface TourPlannerMapper {

    TourPlannerMapper INSTANCE = Mappers.getMapper(TourPlannerMapper.class);
    @Mapping(source = "truck",target = "truck",qualifiedByName = "getTruckID")
    @Mapping(source = "reference", target = "refTour")
    @Mapping(source= "deliverymen",target = "deliverymen", qualifiedByName = "getDeliveryMenIDs")
    TourPlannerResponseDTO toResponse(TourEntity tourEntity);

    //TourEntity  toEntity(TourCreationRequest tourCreationRequest);

    @Named("getTruckID")
    default String getTruckID (TruckEntity truckEntity){
        return truckEntity.getImmatriculation();
    }
    @Named("getDeliveryMenIDs")
    default Set<String> getDeliveryMenIDs(Set<EmployeeEntity> deliverymen){
        return deliverymen.stream().map(EmployeeEntity::getTrigram).collect(Collectors.toSet());
    }
}
