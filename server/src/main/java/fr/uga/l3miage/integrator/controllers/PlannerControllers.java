package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.endpoints.PlannerEndpoints;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.Date;

@Controller
@RequiredArgsConstructor
public class PlannerControllers implements PlannerEndpoints {
    @Override
    public DayResponseDTO getDay(Date date) {

        DayResponseDTO mockResponse= new DayResponseDTO();
        mockResponse.setDate(new LocalDate.now());
        return  mockResponse;
    }

    @Override
    public void planDay(DayCreationRequest dayCreationRequest) {

    }

    @Override
    public SetUpBundleResponse getSetUpBundle() {
        return null;
    }



}
