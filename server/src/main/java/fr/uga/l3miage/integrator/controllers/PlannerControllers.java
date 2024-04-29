package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.endpoints.PlannerEndpoints;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.responses.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class PlannerControllers implements PlannerEndpoints {
    @Override
    public DayResponseDTO getDay(LocalDate date) {
        return  null;
    }

    @Override
    public void planDay(DayCreationRequest dayCreationRequest) {
    }

    @Override
    public SetUpBundleResponse getSetUpBundle() {
        return null;
    }



}
