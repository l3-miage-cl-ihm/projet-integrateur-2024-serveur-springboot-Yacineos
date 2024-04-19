package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import org.mapstruct.Mapper;

@Mapper
public interface TourDMMapper {
    TourDMResponseDTO toResponse(TourEntity tourEntity);

}
