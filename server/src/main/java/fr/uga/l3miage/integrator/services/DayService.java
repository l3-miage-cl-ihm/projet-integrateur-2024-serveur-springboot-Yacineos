package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.DayComponent;
import fr.uga.l3miage.integrator.mappers.DayPlannerMapper;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.repositories.DayRepository;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class DayService {

    private final DayComponent dayComponent;
    private final DayPlannerMapper dayMapper;
    void planDay(DayCreationRequest dayCreationRequest){


    }
    public SetUpBundleResponse getSetUpBundle(){
        return null ;
    }
    public DayResponseDTO getDay(LocalDate date){


            DayEntity day = dayComponent.getDay(date);
            return dayMapper.toResponse(day);

    }
}
