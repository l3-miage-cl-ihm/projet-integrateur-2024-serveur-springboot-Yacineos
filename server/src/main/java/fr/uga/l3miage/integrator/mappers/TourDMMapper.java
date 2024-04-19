package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
@Mapper
public interface TourDMMapper {
    TourDMMapper INSTANCE = Mappers.getMapper(TourDMMapper.class);

    @Mapping(source = "truck.immatriculation", target = "truck")
    @Mapping(source = "reference", target = "refTour")
    @Mapping(source = "reference", target = "refDay" ,qualifiedByName = "extractDayReferenceFromTour")
    @Mapping(source = "warehouseName", target = "warehouseName")
    TourDMResponseDTO toResponse(TourEntity tourEntity);


    default String extractDayReferenceFromTour(String reference) {
        return "J"+reference.substring(1,4);
    }


}
