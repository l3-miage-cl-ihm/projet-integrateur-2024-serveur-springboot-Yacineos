package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.endpoints.PlannerEndpoints;
import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import fr.uga.l3miage.integrator.services.DayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class PlannerControllers implements PlannerEndpoints {

    private final   DayService dayService;
    @Override
    public DayResponseDTO getDay(LocalDate date)  {
        return  dayService.getDay(date);

    }


    @Override
    public void planDay(DayCreationRequest dayCreationRequest) {
        dayService.planDay(dayCreationRequest);
    }

    @Override
    public void editDay(DayCreationRequest dayEditRequest, String dayId) {
            dayService.editDay(dayEditRequest,dayId);
    }

    @Override
    public void updateDayState(String dayId, DayState newDayState) {
        dayService.updateDayState(dayId,newDayState);
    }

    @Override
    public SetUpBundleResponse getSetUpBundle(String warehouseId) {
        return dayService.getSetUpBundle(warehouseId);
    }



}
