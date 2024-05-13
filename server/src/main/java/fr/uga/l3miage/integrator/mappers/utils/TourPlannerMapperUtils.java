package fr.uga.l3miage.integrator.mappers.utils;

import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.exceptions.rest.EntityNotFoundRestException;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.TruckEntity;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
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
public class TourPlannerMapperUtils {

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface GetTruckID{}

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface GetDeliveryMenIDs{}

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

    @GetTruckID
    public String getTruckID (TruckEntity truckEntity){
        return truckEntity.getImmatriculation();
    }
    @GetDeliveryMenIDs
    public Set<String> getDeliveryMenIDs(Set<EmployeeEntity> deliverymen){
        return deliverymen.stream().map(EmployeeEntity::getTrigram).collect(Collectors.toSet());
    }
}
