package fr.uga.l3miage.integrator.mappers.utils;

import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.exceptions.rest.EntityNotFoundRestException;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.models.TruckEntity;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
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
public class DeliveryPlannerMapperUtils {

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface GetOrdersIDs{}


    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface GetDeliveryAddress{}


    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface GetCoord{}

    @GetCoord
    public List<Double> getCord(Coordinates coordinates){
        double lat= coordinates.getLat();
        double lon=coordinates.getLon();
        List<Double> coord=new ArrayList<>();
        coord.add(lat);
        coord.add(lon);
        return coord;
    }

    @GetOrdersIDs
   public Set<String> getOrdersIDs(Set<OrderEntity> orders){
        return   orders.stream().map(OrderEntity::getReference).collect(Collectors.toSet());

    }

    @GetDeliveryAddress
    public String getDeliveryAddress(Set<OrderEntity> orderEntities){
       OrderEntity order =orderEntities.stream().findFirst().orElseThrow(()-> new EntityNotFoundRestException("No Order was found !"));

        Address customerAddress = order.getCustomer().getAddress();
        return customerAddress+","+customerAddress.getCity();
    }


}
