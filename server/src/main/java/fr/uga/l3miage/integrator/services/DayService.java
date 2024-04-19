package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class DayService {
    void planDay(DayCreationRequest dayCreationRequest){

    }
    SetUpBundleResponse getSetUpBundle(){
        return null ;
    }
    DayResponseDTO getDay(Date date){
        return null ;
    }
}
