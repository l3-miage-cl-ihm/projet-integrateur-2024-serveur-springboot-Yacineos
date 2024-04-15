package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.HealthcheckComponent;
import fr.uga.l3miage.integrator.exceptions.rest.HealthcheckRestException;
import fr.uga.l3miage.integrator.exceptions.technical.DataBaseHealthCheckException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HealthcheckService {
    private final HealthcheckComponent healthcheckComponent;

    public void checkServerHealth(){
        try {
            healthcheckComponent.checkHealthDataBase();
        } catch (DataBaseHealthCheckException e) {
            throw new HealthcheckRestException(e.getMessage(), HealthcheckRestException.Type.DATABASE);
        }
    }

}
