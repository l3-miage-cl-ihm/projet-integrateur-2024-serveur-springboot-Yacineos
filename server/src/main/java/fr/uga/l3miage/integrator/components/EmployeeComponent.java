package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EmployeeComponent {
    private final EmployeeRepository employeeRepository;

    public Set<String> getAllDeliveryMenID (){
        Set<EmployeeEntity> employeeEntities = employeeRepository.findEmployeeEntitiesByJob(Job.DELIVERYMAN);
        Set<String> ids = new HashSet<>();
        for (EmployeeEntity employeeEntity :employeeEntities){
            ids.add(employeeEntity.getTrigram());
        }
        return ids;
    }
}
