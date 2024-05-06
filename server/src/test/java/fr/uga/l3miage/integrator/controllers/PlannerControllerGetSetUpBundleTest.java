package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.components.EmployeeComponent;
import fr.uga.l3miage.integrator.components.OrderComponent;
import fr.uga.l3miage.integrator.components.TruckComponent;
import fr.uga.l3miage.integrator.dataTypes.Address;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.CustomerEntity;
import fr.uga.l3miage.integrator.models.EmployeeEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.models.TruckEntity;
import fr.uga.l3miage.integrator.repositories.CustomerRepository;
import fr.uga.l3miage.integrator.repositories.EmployeeRepository;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import fr.uga.l3miage.integrator.repositories.TruckRepository;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import fr.uga.l3miage.integrator.services.DayService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


@AutoConfigureTestDatabase
@AutoConfigureWebClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,properties = {"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect","spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true"})
public class PlannerControllerGetSetUpBundleTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @SpyBean
    private DayService dayService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CustomerRepository customerRepository;
    

    @AfterEach
    void clear(){
        orderRepository.deleteAll();
        truckRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void getSetUpBundle(){

        final HttpHeaders headers = new HttpHeaders();

        Address a1 = new Address("21 rue de la paix","38000","Grenoble");
        Address a2 = new Address("21 rue de la joie","38000","Grenoble");
        CustomerEntity c1 = CustomerEntity.builder()
                .email("mouloud")
                .address(a1)
                .build();
        CustomerEntity c2 = CustomerEntity.builder()
                .email("sur ce le chemin")
                .address(a2)
                .build();
        customerRepository.save(c1);
        customerRepository.save(c2);
        OrderEntity o1 = OrderEntity.builder()
                .reference("c01")
                .creationDate(LocalDate.of(2020, 1, 7))
                .state(OrderState.OPENED)
                .customer(c1)
                .build();
        orderRepository.save(o1);
        OrderEntity o2 = OrderEntity.builder()
                .reference("c02")
                .creationDate(LocalDate.of(2023, 1, 9))
                .customer(c1)
                .state(OrderState.OPENED)
                .build();
        orderRepository.save(o2);
        OrderEntity o3 = OrderEntity.builder()
                .reference("c03")
                .creationDate(LocalDate.of(2020, 1, 8))
                .customer(c2)
                .state(OrderState.OPENED)
                .build();
        orderRepository.save(o3);


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

        employeeRepository.save(e1);
        employeeRepository.save(e2);
        employeeRepository.save(e3);

        TruckEntity t1 = TruckEntity.builder()
                .immatriculation("ABC")
                .build();
        TruckEntity t2 = TruckEntity.builder()
                .immatriculation("DEF")
                .build();

        truckRepository.save(t1);
        truckRepository.save(t2);

        SetUpBundleResponse expectedResponse = dayService.getSetUpBundle();
        ResponseEntity<SetUpBundleResponse> response = testRestTemplate.exchange("/api/v2.0/planner/day/bundle", HttpMethod.GET, new HttpEntity<>(null, headers), SetUpBundleResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(dayService, times(2)).getSetUpBundle();






    }

}
