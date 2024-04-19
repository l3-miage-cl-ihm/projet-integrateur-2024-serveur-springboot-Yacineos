package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(uses = {DeliveryDMMapper.class})
public interface TourDMMapper {
    TourDMMapper INSTANCE = Mappers.getMapper(TourDMMapper.class);

    @Mapping(source = "truck.immatriculation", target = "truck")
    @Mapping(source = "reference", target = "refTour")
    @Mapping(source = "reference", target = "refDay" ,qualifiedByName = "extractDayReferenceFromTour")
    @Mapping(source = "deliverymen", target = "warehouseName",qualifiedByName = "extractWarehouseNameFromDeliveryMan")
    @Mapping(source = "deliverymen", target = "deliverymen",qualifiedByName = "extractFirstAndLastName")
    TourDMResponseDTO toResponse(TourEntity tourEntity);

    @Named("extractDayReferenceFromTour")
    default String extractDayReferenceFromTour(String reference) {
        return "J"+reference.substring(1,4);
    }


    @Named("extractWarehouseNameFromDeliveryMan")
    default  String extractWarehouseNameFromDeliveryMan(Set<EmployeeEntity> deliverymen){
        //deliverymen are necessarily required for a tour
        return deliverymen.stream().findFirst().get().getWarehouse().getName();
    }


    @Named("extractFirstAndLastName")
    default  Set<String> extractFirstAndLastName(Set<EmployeeEntity> deliverymen){
        return   deliverymen.stream().map(deliveryman -> deliveryman.getFirstName()+" "+deliveryman.getLastName()).collect(Collectors.toSet());

    }

}
