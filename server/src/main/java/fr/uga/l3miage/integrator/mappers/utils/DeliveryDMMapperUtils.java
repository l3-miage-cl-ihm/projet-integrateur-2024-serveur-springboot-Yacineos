package fr.uga.l3miage.integrator.mappers.utils;

import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.models.OrderEntity;
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
public class DeliveryDMMapperUtils {

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface ExtractOrderReferences{}


    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface ExtractCustomerName{}

    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface ExtractCustomerAddress{}


    @Qualifier
    @Retention(RetentionPolicy.CLASS)
    @Target(ElementType.METHOD)
    public @interface GetCoord{}


    @ExtractOrderReferences
    public Set<String> extractOrderReferences (Set<OrderEntity> orders){
        return   orders.stream().map(OrderEntity::getReference).collect(Collectors.toSet());

    }

    @ExtractCustomerName
    public String extractCustomerName (Set<OrderEntity> orders){
        return   orders.stream().findFirst().get().getCustomer().getFirstName()+" "+orders.stream().findFirst().get().getCustomer().getLastName();
    }

    @ExtractCustomerAddress
    public String extractCustomerAddress (Set<OrderEntity> orders){
        Address customerAddress=orders.stream().findFirst().get().getCustomer().getAddress();
        return   customerAddress.getAddress()+"|"+customerAddress.getPostalCode()+"|"+customerAddress.getCity();

    }


    @GetCoord
    public List<Double> getCord(Coordinates coordinates){
        double lat= coordinates.getLat();
        double lon=coordinates.getLon();
        List<Double> coord=new ArrayList<>();
        coord.add(lat);
        coord.add(lon);
        return coord;


    }

}
