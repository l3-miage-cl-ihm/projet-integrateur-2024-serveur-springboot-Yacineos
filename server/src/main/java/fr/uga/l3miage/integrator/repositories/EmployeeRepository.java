package fr.uga.l3miage.integrator.repositories;

import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.WarehouseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity,String> {
    Set<EmployeeEntity> findEmployeeEntitiesByJobAndWarehouse(Job job, WarehouseEntity warehouse);

    Optional<EmployeeEntity> findByEmail(String email);
}
