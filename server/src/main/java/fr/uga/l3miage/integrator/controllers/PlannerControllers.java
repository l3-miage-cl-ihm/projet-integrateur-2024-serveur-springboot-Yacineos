package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.endpoints.PlannerEndpoints;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.responses.*;
import fr.uga.l3miage.integrator.services.DayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class PlannerControllers implements PlannerEndpoints {

    private final   DayService dayService;
    @Override
    public DayResponseDTO getDay(LocalDate date) {
        //call the service
        return  dayService.getDay(date);
    }
    @Override
    public void planDay(DayCreationRequest dayCreationRequest) {
        dayService.planDay(dayCreationRequest);
    }
    @Override
    public SetUpBundleResponse getSetUpBundle() {
        //call the service
        return null;
    }



}
