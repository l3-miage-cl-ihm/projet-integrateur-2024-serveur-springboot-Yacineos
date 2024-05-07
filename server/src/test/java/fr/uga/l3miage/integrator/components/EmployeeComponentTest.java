package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.repositories.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class EmployeeComponentTest {

    @Autowired
    private EmployeeComponent employeeComponent;

    @MockBean
    private EmployeeRepository employeeRepository;


    @Test
    void getAllDeliveryMenID(){
        EmployeeEntity e1 = EmployeeEntity.builder()
                .job(Job.DELIVERYMAN)
                .trigram("ABC")
                .build();
        EmployeeEntity e2 = EmployeeEntity.builder()
                .job(Job.DELIVERYMAN)
                .trigram("DEF")
                .build();
        EmployeeEntity e3 = EmployeeEntity.builder()
                .job(Job.DELIVERYMAN)
                .trigram("GHI")
                .build();
        EmployeeEntity e4 = EmployeeEntity.builder()
                .job(Job.PRODUCTOR)
                .trigram("JKL")
                .build();
        Set<EmployeeEntity> employeeEntities = new HashSet<>();
        employeeEntities.add(e1);
        employeeEntities.add(e2);
        employeeEntities.add(e3);

        Set<String> employeeID = new HashSet<>();
        employeeID.add(e1.getTrigram());
        employeeID.add(e2.getTrigram());
        employeeID.add(e3.getTrigram());

        when(employeeRepository.findEmployeeEntitiesByJob(Job.DELIVERYMAN)).thenReturn(employeeEntities);
        Set<String> stringSet = employeeComponent.getAllDeliveryMenID();

        assertThat(stringSet.size()).isEqualTo(3);
        assertThat(stringSet.stream().findFirst()).isEqualTo(employeeID.stream().findFirst());

    }
}
