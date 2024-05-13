package fr.uga.l3miage.integrator.mappers.utils;

import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TourDMMapperUtils {
    @ExtractDayReferenceFromTour
    public String extractDayReferenceFromTour(String reference) {
        return "J"+reference.substring(1,4);
    }

    @ExtractWarehouseNameFromDeliveryMan
    public  String extractWarehouseNameFromDeliveryMan(Set<EmployeeEntity> deliverymen){
        //deliverymen are necessarily required for a tour.
        return deliverymen.stream().findFirst().get().getWarehouse().getName();
    }
    @ExtractFirstAndLastName
    public   Set<String> extractFirstAndLastName(Set<EmployeeEntity> deliverymen){
        return   deliverymen.stream().map(deliveryman -> deliveryman.getFirstName()+" "+deliveryman.getLastName()).collect(Collectors.toSet());

    }



    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface ExtractDayReferenceFromTour{}



    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface ExtractWarehouseNameFromDeliveryMan{}

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface ExtractFirstAndLastName{}








}
