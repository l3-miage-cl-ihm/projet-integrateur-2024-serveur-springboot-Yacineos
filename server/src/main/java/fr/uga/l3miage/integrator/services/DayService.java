package fr.uga.l3miage.integrator.services;

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
    public void planDay(DayCreationRequest dayCreationRequest){

    }
    public SetUpBundleResponse getSetUpBundle(){
        return null ;
    }
    public DayResponseDTO getDay(LocalDate date){
        return null ;
    }
}
