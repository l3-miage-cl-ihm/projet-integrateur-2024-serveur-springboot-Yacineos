package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.endpoints.HealthcheckEndpoints;
import fr.uga.l3miage.integrator.services.HealthcheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor
@Controller
public class HealthcheckController implements HealthcheckEndpoints {
    private final HealthcheckService healthcheckService;
    @Override
    public void healthcheck() {
        healthcheckService.checkServerHealth();
    }
}
