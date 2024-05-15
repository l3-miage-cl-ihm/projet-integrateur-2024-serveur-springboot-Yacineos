package fr.uga.l3miage.integrator.repositories;

import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.WarehouseEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
@AutoConfigureTestDatabase
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;


    @Test
    void findEmployeeEntitiesByJobAndWarehouse(){

        WarehouseEntity warehouse=WarehouseEntity.builder().name("Grenis")
                .letter("G")
                .address(new Address("02 rue des croissants","36983","Monaco"))
                .trucks(Set.of())
                .days(Set.of())
                .coordinates(new Coordinates(23.84,76.302))
                .photo(".png")
                .build();
        warehouseRepository.save(warehouse);

        EmployeeEntity e1 = EmployeeEntity.builder().trigram("abc").job(Job.DELIVERYMAN).warehouse(warehouse).build();
        EmployeeEntity e2 = EmployeeEntity.builder().trigram("cde").job(Job.DELIVERYMAN).warehouse(warehouse).build();
        EmployeeEntity e3 = EmployeeEntity.builder().trigram("fgh").job(Job.DELIVERYMAN).warehouse(warehouse).build();
        EmployeeEntity e4 = EmployeeEntity.builder().trigram("ijk").job(Job.PRODUCTOR).warehouse(warehouse).build();
        employeeRepository.save(e1);
        employeeRepository.save(e2);
        employeeRepository.save(e3);
        employeeRepository.save(e4);

        Set<EmployeeEntity> employeeEntities = employeeRepository.findEmployeeEntitiesByJobAndWarehouse(Job.DELIVERYMAN,warehouse);

        assertThat(employeeEntities.size()).isEqualTo(3);
        assertThat(employeeEntities.stream().findFirst().get().getTrigram()).isEqualTo(e1.getTrigram());


    }
}
