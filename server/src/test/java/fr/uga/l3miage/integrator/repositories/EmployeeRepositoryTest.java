package fr.uga.l3miage.integrator.repositories;

import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
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


    @Test
    void findEmployeeEntitiesByJob(){

        EmployeeEntity e1 = EmployeeEntity.builder()
                .trigram("abc")
                .job(Job.DELIVERYMAN)
                .build();
        EmployeeEntity e2 = EmployeeEntity.builder()
                .trigram("cde")
                .job(Job.DELIVERYMAN)
                .build();
        EmployeeEntity e3 = EmployeeEntity.builder()
                .trigram("fgh")
                .job(Job.DELIVERYMAN)
                .build();
        EmployeeEntity e4 = EmployeeEntity.builder()
                .trigram("ijk")
                .job(Job.PRODUCTOR)
                .build();

        employeeRepository.save(e1);
        employeeRepository.save(e2);
        employeeRepository.save(e3);
        employeeRepository.save(e4);

        Set<EmployeeEntity> employeeEntities = employeeRepository.findEmployeeEntitiesByJob(Job.DELIVERYMAN);

        assertThat(employeeEntities.size()).isEqualTo(3);
        assertThat(employeeEntities.stream().findFirst().get().getTrigram()).isEqualTo(e1.getTrigram());


    }
}
