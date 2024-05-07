package fr.uga.l3miage.integrator.mappers.utils;

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

    @GetTruckID
    public String getTruckID (TruckEntity truckEntity){
        return truckEntity.getImmatriculation();
    }
    @GetDeliveryMenIDs
    public Set<String> getDeliveryMenIDs(Set<EmployeeEntity> deliverymen){
        return deliverymen.stream().map(EmployeeEntity::getTrigram).collect(Collectors.toSet());
    }
}
