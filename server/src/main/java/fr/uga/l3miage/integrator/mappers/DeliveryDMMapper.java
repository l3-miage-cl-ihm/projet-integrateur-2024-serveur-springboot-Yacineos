package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.mappers.utils.TourDMMapperUtils;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.responses.DeliveryDMResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {TourDMMapperUtils.class})
public interface DeliveryDMMapper {
    @Mapping(source = "reference", target = "deliveryId")
    @Mapping(source = "orders", target = "orders",qualifiedBy= TourDMMapperUtils.ExtractOrderReferences.class)
    @Mapping(source = "orders", target = "customer",qualifiedBy=TourDMMapperUtils.ExtractCustomerName.class)
    @Mapping(source = "orders", target ="customerAddress",qualifiedBy=TourDMMapperUtils.ExtractCustomerAddress.class)
    DeliveryDMResponseDTO toResponse(DeliveryEntity deliveryEntity);

}





