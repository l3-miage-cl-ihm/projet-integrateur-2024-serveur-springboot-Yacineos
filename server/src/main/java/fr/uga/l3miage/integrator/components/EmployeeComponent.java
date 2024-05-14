package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.exceptions.technical.WarehouseNotFoundException;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.WarehouseEntity;
import fr.uga.l3miage.integrator.repositories.EmployeeRepository;
import fr.uga.l3miage.integrator.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class EmployeeComponent {
    private final EmployeeRepository employeeRepository;
    private final WarehouseRepository warehouseRepository;

    public Set<String> getAllDeliveryMenID (String idWarehouse){
        WarehouseEntity warehouseEntity = warehouseRepository.findById(idWarehouse).orElseThrow(()-> new WarehouseNotFoundException("aucun entrepot trouvé"));
        Set<EmployeeEntity> employeeEntities = employeeRepository.findEmployeeEntitiesByJobAndWarehouse(Job.DELIVERYMAN, warehouseEntity);
        Set<String> ids = new HashSet<>();
        for (EmployeeEntity employeeEntity :employeeEntities){
            ids.add(employeeEntity.getTrigram());
        }
        return ids;
    }


    public Job getEmployeeJobFromEmail(String email ){
        EmployeeEntity employee = this.employeeRepository.findByEmail(email).orElseThrow();
        return employee.getJob();
    }
}
