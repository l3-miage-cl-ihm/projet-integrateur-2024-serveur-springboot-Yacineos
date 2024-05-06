package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.EmployeeComponent;
import fr.uga.l3miage.integrator.components.OrderComponent;
import fr.uga.l3miage.integrator.components.TruckComponent;
import fr.uga.l3miage.integrator.dataTypes.Address;
import fr.uga.l3miage.integrator.dataTypes.MultipleOrder;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.CustomerEntity;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.models.TruckEntity;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DayServiceSetupBundleTest {

    @Autowired
    private DayService dayService;

    @MockBean
    private EmployeeComponent employeeComponent;

    @MockBean
    private OrderComponent orderComponent;

    @MockBean
    private TruckComponent truckComponent;


    @Test
    void getSetUpBundle(){

        Address a1 = new Address("21 rue de la paix","38000","Grenoble");
        Address a2 = new Address("21 rue de la joie","38000","Grenoble");
        CustomerEntity c1 = CustomerEntity.builder()
                .address(a1)
                .build();
        CustomerEntity c2 = CustomerEntity.builder()
                .address(a2)
                .build();
        OrderEntity o1 = OrderEntity.builder()
                .reference("c01")
                .creationDate(LocalDate.of(2020, 1, 7))
                .state(OrderState.OPENED)
                .customer(c1)
                .build();
        OrderEntity o2 = OrderEntity.builder()
                .reference("c02")
                .creationDate(LocalDate.of(2023, 1, 9))
                .customer(c1)
                .state(OrderState.OPENED)
                .build();
        OrderEntity o3 = OrderEntity.builder()
                .reference("c03")
                .creationDate(LocalDate.of(2024, 1, 8))
                .customer(c2)
                .state(OrderState.OPENED)
                .build();


        EmployeeEntity e1 = EmployeeEntity.builder()
                .job(Job.DELIVERYMAN)
                .trigram("abc")
                .build();
        EmployeeEntity e2 = EmployeeEntity.builder()
                .job(Job.DELIVERYMAN)
                .trigram("def")
                .build();
        EmployeeEntity e3 = EmployeeEntity.builder()
                .job(Job.DELIVERYMAN)
                .trigram("ghi")
                .build();

        TruckEntity t1 = TruckEntity.builder()
                .immatriculation("ABC")
                .build();
        TruckEntity t2 = TruckEntity.builder()
                .immatriculation("DEF")
                .build();

        MultipleOrder m1 = new MultipleOrder(Set.of(o1.getReference(),o2.getReference()),a1.toString());
        MultipleOrder m2 = new MultipleOrder(Set.of(o3.getReference()),a2.toString());
        Set<MultipleOrder> m3 = new HashSet<>();
        m3.add(m1);
        m3.add(m2);

        Set<String> truckImmatriculations = new HashSet<>();
        truckImmatriculations.add(t1.getImmatriculation());
        truckImmatriculations.add(t2.getImmatriculation());

        Set<String> employeeIds = new HashSet<>();
        employeeIds.add(e1.getTrigram());
        employeeIds.add(e2.getTrigram());
        employeeIds.add(e3.getTrigram());

        when(employeeComponent.getAllDeliveryMenID()).thenReturn(employeeIds);
        when(truckComponent.getAllTrucksImmatriculation()).thenReturn(truckImmatriculations);
        when(orderComponent.createMultipleOrders()).thenReturn(m3);
        SetUpBundleResponse response = dayService.getSetUpBundle();

        assertThat(response.getDeliverymen().size()).isEqualTo(3);
        assertThat(response.getMultipleOrders().size()).isEqualTo(2);
        assertThat(response.getTruck().size()).isEqualTo(2);
        assertThat(response.getMultipleOrders().stream().findFirst().get()).isEqualTo("[[c01,c02],21 rue de la paix, Grenoble]");

    }
}
