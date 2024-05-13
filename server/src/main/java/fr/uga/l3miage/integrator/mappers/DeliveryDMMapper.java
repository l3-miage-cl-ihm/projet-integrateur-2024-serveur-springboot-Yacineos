package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.mappers.utils.DeliveryDMMapperUtils;
import fr.uga.l3miage.integrator.mappers.utils.TourDMMapperUtils;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.responses.DeliveryDMResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DeliveryDMMapperUtils.class})
public interface DeliveryDMMapper {
    @Mapping(source = "reference", target = "deliveryId")
    @Mapping(source = "orders", target = "orders",qualifiedBy= DeliveryDMMapperUtils.ExtractOrderReferences.class)
    @Mapping(source = "orders", target = "customer",qualifiedBy=DeliveryDMMapperUtils.ExtractCustomerName.class)
    @Mapping(source = "orders", target ="customerAddress",qualifiedBy=DeliveryDMMapperUtils.ExtractCustomerAddress.class)
    @Mapping(source = "coordinates", target = "coordinates",qualifiedBy = DeliveryDMMapperUtils.GetCoord.class)
    DeliveryDMResponseDTO toResponse(DeliveryEntity deliveryEntity);

}





