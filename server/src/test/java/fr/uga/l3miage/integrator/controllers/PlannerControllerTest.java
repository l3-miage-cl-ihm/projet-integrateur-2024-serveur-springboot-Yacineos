package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.CustomerState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@AutoConfigureTestDatabase
@AutoConfigureWebClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,properties = {"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect","spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true"})
public class PlannerControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private DayRepository dayRepository;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @SpyBean
    private DayService dayService;

    @AfterEach
    void clear(){
        employeeRepository.findAll().forEach(employee -> {employee.setWarehouse(null); employeeRepository.save(employee);} );
        warehouseRepository.deleteAll();
        dayRepository.deleteAll();
        tourRepository.deleteAll();
        deliveryRepository.deleteAll();
        orderRepository.deleteAll();
        truckRepository.deleteAll();
        employeeRepository.deleteAll();
        customerRepository.deleteAll();

    }

    @Test
    void planDayOK(){
       //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();

        //IMPORTANT: do not forget the planner because as decided before, he is one to manage the warehouse and it is loaded in the DayPlannerMapper
        WarehouseEntity grenis = WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards", "65001", "San antonio")).trucks(Set.of()).build();
        warehouseRepository.save(grenis);
        EmployeeEntity planner = EmployeeEntity.builder().email("claudiatessiere@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("TESSIERE").firstName("claudia").mobilePhone("0765437876").trigram("STR").warehouse(grenis).build();
        employeeRepository.save(planner);

        Set<TourCreationRequest> tourCreationRequestSet= new HashSet<>();
        TruckEntity truck= TruckEntity.builder().immatriculation("AY-124-GF").build();
        truckRepository.save(truck);
        EmployeeEntity deliveryman1= EmployeeEntity.builder()
                .email("samy@gmail.com")
                .job(Job.DELIVERYMAN)
                .trigram("SSA")
                .firstName("Samy")
                .lastName("Silva")
                .mobilePhone("0654326754")
                .photo("samy.png")
                .build();
        EmployeeEntity deliveryman2= EmployeeEntity.builder()
                .email("jugurta@gmail.com")
                .job(Job.DELIVERYMAN)
                .trigram("JOK")
                .firstName("Jugurta")
                .lastName("Ourzik")
                .mobilePhone("065432354")
                .photo("juju.png")
                .build();

        employeeRepository.save(deliveryman1);
        employeeRepository.save(deliveryman2);
        CustomerEntity customer1= CustomerEntity.builder()
                .email("claire@gmail.com")
                .firstName("Claire")
                .lastName("Wyz")
                .address(new Address("2 Avenue des riches","75001","Paris"))
                .state(CustomerState.DELIVERABLE)
                .build();

        CustomerEntity customer2= CustomerEntity.builder()
                .email("caddy@gmail.com")
                .firstName("wyz")
                .lastName("Cyz")
                .address(new Address("2 Avenue des riches","75001","Paris"))
                .state(CustomerState.DELIVERABLE)
                .build();
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        OrderEntity o1= OrderEntity.builder()
                .reference("c001")
                .creationDate(LocalDate.now())
                .state(OrderState.OPENED)
                .lines(Set.of())
                .customer(customer1)
                .build();
        OrderEntity o2= OrderEntity.builder()
                .reference("c002")
                .creationDate(LocalDate.now())
                .state(OrderState.OPENED)
                .lines(Set.of())
                .customer(customer1)
                .build();

        OrderEntity o3= OrderEntity.builder()
                .reference("c003")
                .creationDate(LocalDate.now())
                .state(OrderState.OPENED)
                .lines(Set.of())
                .customer(customer2)
                .build();
        orderRepository.save(o1);
        orderRepository.save(o2);
        orderRepository.save(o3);
        Set<DeliveryCreationRequest> deliveryCreationRequestSet=new HashSet<>();
        DeliveryCreationRequest d1= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o1.getReference(),o2.getReference()))
                .build();

        DeliveryCreationRequest d2= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o3.getReference()))
                .build();
        deliveryCreationRequestSet.add(d1);
        deliveryCreationRequestSet.add(d2);
        TourCreationRequest t1= TourCreationRequest.builder()
                .distanceToCover(24)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(),deliveryman2.getTrigram()))
                .deliveries(deliveryCreationRequestSet)
                .build();

        tourCreationRequestSet.add(t1);
        DayCreationRequest dayCreationRequest= DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(tourCreationRequestSet)
                .build();


        //when
        //dayService.planDay(dayCreationRequest);  //Here dayService returns nothing but the call can help to verify others repositories, components or mappers apparences.
        ResponseEntity<Void> response=testRestTemplate.exchange("/api/v2.0/planner/day/plan", HttpMethod.POST, new HttpEntity<>(dayCreationRequest, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(dayService,times(1)).planDay(dayCreationRequest);



    }



    @Test  //simulating day plannification without any tour
    void planDay_NotOK_BecauseOfMissedInputs(){
            //given
            final HttpHeaders headers = new HttpHeaders();

            final Map<String, Object> urlParams = new HashMap<>();

            DayCreationRequest dayCreationRequest= DayCreationRequest.builder()
                    .date(LocalDate.now())
                    .tours(Set.of())
                    .build();


            //when
            ResponseEntity<Void> response=testRestTemplate.exchange("/api/v2.0/planner/day/plan", HttpMethod.POST, new HttpEntity<>(dayCreationRequest, headers), Void.class, urlParams);

            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
            verify(dayService,times(1)).planDay(dayCreationRequest);



        }

    @Test  //simulating wrong deliveryman trigram input
    void planDat_NotOK_BecauseOfWrongInputs1() {

        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();

        //IMPORTANT: do not forget the planner because as decided before, he is one to manage the warehouse and it is loaded in the DayPlannerMapper
        WarehouseEntity grenis = WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards", "65001", "San antonio")).trucks(Set.of()).build();
        warehouseRepository.save(grenis);
        EmployeeEntity planner = EmployeeEntity.builder().email("claudiatessiere@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("TESSIERE").firstName("claudia").mobilePhone("0765437876").trigram("STR").warehouse(grenis).build();
        employeeRepository.save(planner);

        Set<TourCreationRequest> tourCreationRequestSet = new HashSet<>();
        TruckEntity truck = TruckEntity.builder().immatriculation("AY-124-GF").build();
        truckRepository.save(truck);
        EmployeeEntity deliveryman1 = EmployeeEntity.builder()
                .email("samy@gmail.com")
                .job(Job.DELIVERYMAN)
                .trigram("SSA")
                .firstName("Samy")
                .lastName("Silva")
                .mobilePhone("0654326754")
                .photo("samy.png")
                .build();

        employeeRepository.save(deliveryman1);
        CustomerEntity customer1 = CustomerEntity.builder()
                .email("claire@gmail.com")
                .firstName("Claire")
                .lastName("Wyz")
                .address(new Address("2 Avenue des riches", "75001", "Paris"))
                .state(CustomerState.DELIVERABLE)
                .build();

        CustomerEntity customer2 = CustomerEntity.builder()
                .email("caddy@gmail.com")
                .firstName("wyz")
                .lastName("Cyz")
                .address(new Address("2 Avenue des riches", "75001", "Paris"))
                .state(CustomerState.DELIVERABLE)
                .build();
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        OrderEntity o1 = OrderEntity.builder()
                .reference("c001")
                .creationDate(LocalDate.now())
                .state(OrderState.OPENED)
                .lines(Set.of())
                .customer(customer1)
                .build();
        OrderEntity o2 = OrderEntity.builder()
                .reference("c002")
                .creationDate(LocalDate.now())
                .state(OrderState.OPENED)
                .lines(Set.of())
                .customer(customer1)
                .build();

        OrderEntity o3 = OrderEntity.builder()
                .reference("c003")
                .creationDate(LocalDate.now())
                .state(OrderState.OPENED)
                .lines(Set.of())
                .customer(customer2)
                .build();
        orderRepository.save(o1);
        orderRepository.save(o2);
        orderRepository.save(o3);
        Set<DeliveryCreationRequest> deliveryCreationRequestSet = new HashSet<>();
        DeliveryCreationRequest d1 = DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o1.getReference(), o2.getReference()))
                .build();

        DeliveryCreationRequest d2 = DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o3.getReference()))
                .build();
        deliveryCreationRequestSet.add(d1);
        deliveryCreationRequestSet.add(d2);
        TourCreationRequest t1 = TourCreationRequest.builder()
                .distanceToCover(24)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(), "YTR")) //no existing deliveryman with trigram 'YTR'
                .deliveries(deliveryCreationRequestSet)
                .build();

        tourCreationRequestSet.add(t1);
        DayCreationRequest dayCreationRequest = DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(tourCreationRequestSet)
                .build();


        //when
        //dayService.planDay(dayCreationRequest);  //Here dayService returns nothing but the call can help to verify others repositories, components or mappers apparences.
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v2.0/planner/day/plan", HttpMethod.POST, new HttpEntity<>(dayCreationRequest, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        verify(dayService, times(1)).planDay(dayCreationRequest);

    }


    @Test  //simulating wrong order reference input
    void planDat_NotOK_BecauseOfWrongInputs2() {

        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();

        //IMPORTANT: do not forget the planner because as decided before, he is one to manage the warehouse and it is created in the DayPlannerMapper
        WarehouseEntity grenis = WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards", "65001", "San antonio")).trucks(Set.of()).build();
        warehouseRepository.save(grenis);
        EmployeeEntity planner = EmployeeEntity.builder().email("christian.paul@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("Paul").firstName("Christian").mobilePhone("0765437876").trigram("STR").warehouse(grenis).build();
        employeeRepository.save(planner);

        Set<TourCreationRequest> tourCreationRequestSet = new HashSet<>();
        TruckEntity truck = TruckEntity.builder().immatriculation("AY-124-GF").build();
        truckRepository.save(truck);
        EmployeeEntity deliveryman1 = EmployeeEntity.builder()
                .email("samy@gmail.com")
                .job(Job.DELIVERYMAN)
                .trigram("SSA")
                .firstName("Samy")
                .lastName("Silva")
                .mobilePhone("0654326754")
                .photo("samy.png")
                .build();
        EmployeeEntity deliveryman2= EmployeeEntity.builder()
                .email("jugurta@gmail.com")
                .job(Job.DELIVERYMAN)
                .trigram("JOK")
                .firstName("Jugurta")
                .lastName("Ourzik")
                .mobilePhone("065432354")
                .photo("juju.png")
                .build();
        employeeRepository.save(deliveryman1);
        employeeRepository.save(deliveryman2);
        CustomerEntity customer1 = CustomerEntity.builder()
                .email("claire@gmail.com")
                .firstName("Claire")
                .lastName("Wyz")
                .address(new Address("2 Avenue des riches", "75001", "Paris"))
                .state(CustomerState.DELIVERABLE)
                .build();

        CustomerEntity customer2 = CustomerEntity.builder()
                .email("caddy@gmail.com")
                .firstName("wyz")
                .lastName("Cyz")
                .address(new Address("2 Avenue des riches", "75001", "Paris"))
                .state(CustomerState.DELIVERABLE)
                .build();
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        OrderEntity o1 = OrderEntity.builder()
                .reference("c001")
                .creationDate(LocalDate.now())
                .state(OrderState.OPENED)
                .lines(Set.of())
                .customer(customer1)
                .build();
        OrderEntity o2 = OrderEntity.builder()
                .reference("c002")
                .creationDate(LocalDate.now())
                .state(OrderState.OPENED)
                .lines(Set.of())
                .customer(customer1)
                .build();

        OrderEntity o3 = OrderEntity.builder()
                .reference("c003")
                .creationDate(LocalDate.now())
                .state(OrderState.OPENED)
                .lines(Set.of())
                .customer(customer2)
                .build();
        orderRepository.save(o1);
        orderRepository.save(o2);
        orderRepository.save(o3);
        Set<DeliveryCreationRequest> deliveryCreationRequestSet = new HashSet<>();
        DeliveryCreationRequest d1 = DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o1.getReference(), o2.getReference()))
                .build();

        DeliveryCreationRequest d2 = DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of("c900")) //no existing order with given reference c170
                .build();
        deliveryCreationRequestSet.add(d1);
        deliveryCreationRequestSet.add(d2);
        TourCreationRequest t1 = TourCreationRequest.builder()
                .distanceToCover(24)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(), deliveryman2.getTrigram()))
                .deliveries(deliveryCreationRequestSet)
                .build();

        tourCreationRequestSet.add(t1);
        DayCreationRequest dayCreationRequest = DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(tourCreationRequestSet)
                .build();


        //when
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v2.0/planner/day/plan", HttpMethod.POST, new HttpEntity<>(dayCreationRequest, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        verify(dayService, times(1)).planDay(dayCreationRequest);

    }

}
