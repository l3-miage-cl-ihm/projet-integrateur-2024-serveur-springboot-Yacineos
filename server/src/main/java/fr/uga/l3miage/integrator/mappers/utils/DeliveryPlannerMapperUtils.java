package fr.uga.l3miage.integrator.mappers.utils;

import fr.uga.l3miage.integrator.datatypes.Address;
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

    @GetOrdersIDs
   public Set<String> getOrdersIDs(Set<OrderEntity> orders){
        return   orders.stream().map(OrderEntity::getReference).collect(Collectors.toSet());

    }

    @GetDeliveryAddress
    public String GetDeliveryAddress(Set<OrderEntity> orderEntities){
        Address customerAddress=orderEntities.stream().findFirst().get().getCustomer().getAddress();
        return customerAddress.getAddress()+","+customerAddress.getCity();
    }


}
