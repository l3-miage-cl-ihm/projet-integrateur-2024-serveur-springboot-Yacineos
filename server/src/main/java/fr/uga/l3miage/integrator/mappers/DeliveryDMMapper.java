package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.dataTypes.Address;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.responses.DeliveryDMResponseDTO;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;


@Mapper
public interface DeliveryDMMapper {
    DeliveryDMMapper INSTANCE = Mappers.getMapper(DeliveryDMMapper.class);


    @Mapping(source = "reference", target = "deliveryId")
    @Mapping(source = "orders", target = "orders",qualifiedByName = "extractOrderReferences")
    @Mapping(source = "orders", target = "customer",qualifiedByName = "extractCustomerName")
    @Mapping(source = "orders", target ="customerAddress",qualifiedByName = "extractCustomerAddress")
    DeliveryDMResponseDTO toResponse(DeliveryEntity deliveryEntity);


    @Named("extractOrderReferences")
    default Set<String> extractOrderReferences (Set<OrderEntity> orders){
        return   orders.stream().map( order -> order.getReference()).collect(Collectors.toSet());

    }

    @Named("extractCustomerName")
    default String extractCustomerName (Set<OrderEntity> orders){
        return   orders.stream().findFirst().get().getCustomer().getFirstName()+" "+orders.stream().findFirst().get().getCustomer().getLastName();

    }

    @Named("extractCustomerAddress")
    default String extractCustomerAddress (Set<OrderEntity> orders){
        Address customerAddress=orders.stream().findFirst().get().getCustomer().getAddress();
        return   customerAddress.getAddress()+"|"+customerAddress.getPostalCode()+"|"+customerAddress.getCity();

    }



}





