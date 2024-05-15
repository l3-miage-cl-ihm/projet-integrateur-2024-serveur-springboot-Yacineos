package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.mappers.utils.TourPlannerMapperUtils;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.TourPlannerResponseDTO;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DeliveryPlannerMapper.class, TourPlannerMapperUtils.class})
@DecoratedWith(TourPlannerMapperDecorator.class)
public interface TourPlannerMapper {

    @Mapping(source = "truck",target = "truck",qualifiedBy=TourPlannerMapperUtils.GetTruckID.class)
    @Mapping(source = "reference", target = "refTour")
    @Mapping(source= "deliverymen",target = "deliverymen", qualifiedBy=TourPlannerMapperUtils.GetDeliveryMenIDs.class)
    @Mapping(target = "deliveries",ignore = true)
    @Mapping(source = "deliverymen" ,target = "coordinates",qualifiedBy = TourPlannerMapperUtils.GetWarehouseCoordinates.class)
    TourPlannerResponseDTO toResponse(TourEntity tourEntity);



    @Mapping(target = "deliverymen", ignore=true)
    @Mapping(target = "deliveries", ignore=true)
    @Mapping(target = "reference", ignore=true)
    @Mapping(target = "state", ignore=true)
    @Mapping(target = "letter", ignore=true)
    @Mapping(target = "actualAssemblyTime", ignore=true)
    @Mapping(target = "truck", ignore=true)
    TourEntity  toEntity(TourCreationRequest tourCreationRequest,String tourRef) throws InvalidInputValueException;



}
