package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.mappers.utils.TourDMMapperUtils;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(uses = {DeliveryDMMapper.class, TourDMMapperUtils.class})
public interface TourDMMapper {
    TourDMMapper INSTANCE = Mappers.getMapper(TourDMMapper.class);

    @Mapping(source = "truck.immatriculation", target = "truck")
    @Mapping(source = "reference", target = "refTour")
    @Mapping(source = "reference", target = "refDay" , qualifiedBy = TourDMMapperUtils.ExtractDayReferenceFromTour.class)
    @Mapping(source = "deliverymen", target = "warehouseName",qualifiedBy=TourDMMapperUtils.ExtractWarehouseNameFromDeliveryMan.class)
    @Mapping(source = "deliverymen", target = "deliverymen",qualifiedBy=TourDMMapperUtils.ExtractFirstAndLastName.class)
    TourDMResponseDTO toResponse(TourEntity tourEntity);





}
