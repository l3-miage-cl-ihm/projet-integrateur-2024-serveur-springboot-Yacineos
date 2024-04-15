package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.exceptions.technical.DataBaseHealthCheckException;
import fr.uga.l3miage.integrator.models.HealthcheckEntity;
import fr.uga.l3miage.integrator.repositories.HealthcheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HealthcheckComponent {
    private final HealthcheckRepository healthcheckRepository;


    public void checkHealthDataBase() throws DataBaseHealthCheckException {
        HealthcheckEntity entity = HealthcheckEntity.builder().id(1L).build();
        healthcheckRepository.save(entity);
        healthcheckRepository.findById(1L).orElseThrow(()-> new DataBaseHealthCheckException("La base ne trouve pas l'élément ajouté precédement"));
        healthcheckRepository.deleteAll();
    }
}
