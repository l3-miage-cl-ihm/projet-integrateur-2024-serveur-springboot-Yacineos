package fr.uga.l3miage.integrator.repositories;

import fr.uga.l3miage.integrator.models.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmployeeRepository extends JpaRepository<EmployeeEntity,String> {
}
