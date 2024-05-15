package fr.uga.l3miage.integrator.mappers.utils;

import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.exceptions.rest.EntityNotFoundRestException;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Qualifier;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
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

    @GetWarehouseCoordinates
    public List<Double> getWarehouseCoordinates(Set<EmployeeEntity> deliverymen){
        List<Double> coord=new ArrayList<>();
        EmployeeEntity deliveryman =deliverymen.stream().findFirst().orElseThrow(()-> new EntityNotFoundRestException("No deliveryman was found !"));
       Coordinates coordinates =deliveryman.getWarehouse().getCoordinates();
        coord.add(coordinates.getLat());
        coord.add(coordinates.getLon());
        return coord;
    }

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface GetWarehouseCoordinates{}


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
