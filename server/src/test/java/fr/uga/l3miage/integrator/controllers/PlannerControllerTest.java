package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.components.DayComponent;
import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.CustomerState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.exceptions.NotFoundErrorResponse;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import fr.uga.l3miage.integrator.services.DayService;
import org.assertj.core.api.AssertionsForClassTypes;
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
import static org.mockito.ArgumentMatchers.any;
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
    @SpyBean
    private DayComponent dayComponent;

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



    @Test
    void getDayOK() throws DayNotFoundException {
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("date", LocalDate.of(2024,4,29).toString());

        //given
        // creation deliveryMen 1
        Set<TourEntity> tours= new HashSet<>();
        Set<EmployeeEntity> deliverymen= new HashSet<>();
        EmployeeEntity m1=EmployeeEntity.builder().trigram("jjo").email("jojo@gmail.com").build();
        EmployeeEntity m2=EmployeeEntity.builder().trigram("axl").email("axel@gmail.com").build();
        employeeRepository.save(m1);
        employeeRepository.save(m2);
        deliverymen.add(m1);
        deliverymen.add(m2);
        //Creation delivery 1
        //creation order
        OrderEntity order11=OrderEntity.builder().reference("c11").build();
        OrderEntity order12=OrderEntity.builder().reference("c12").build();
        orderRepository.save(order11);
        orderRepository.save(order12);
        Set<OrderEntity> orders1 = new HashSet<>();
        orders1.add(order11);
        orders1.add(order12);
        DeliveryEntity del1=DeliveryEntity.builder().reference("T238G-A1").build();
        del1.setOrders(orders1);
        deliveryRepository.save(del1);
        //Creation delivery 2
        //creation order
        OrderEntity order21=OrderEntity.builder().reference("c21").build();
        OrderEntity order22=OrderEntity.builder().reference("c22").build();
        orderRepository.save(order21);
        orderRepository.save(order22);
        Set<OrderEntity> orders2 = new HashSet<>();
        orders2.add(order11);
        orders2.add(order12);
        DeliveryEntity del2=DeliveryEntity.builder().reference("T238G-A2").build();
        del2.setOrders(orders2);
        deliveryRepository.save(del2);
        Set<DeliveryEntity> deliveries1=new HashSet<>();
        deliveries1.add(del1);
        deliveries1.add(del2);
        //creation tour 1
        TruckEntity truck1=TruckEntity.builder().immatriculation("EV-666-IL").build();
        truckRepository.save(truck1);
        TourEntity tour1= TourEntity.builder().reference("T238G-A").build();
        tour1.setDeliverymen(deliverymen);
        tour1.setTruck(truck1);
        tour1.setDeliveries(deliveries1);
        tourRepository.save(tour1);
        tours.add(tour1);
        // creation deliveryMen 2
        Set<EmployeeEntity> deliverymen2= new HashSet<>();
        EmployeeEntity m3=EmployeeEntity.builder().trigram("jju").email("juju@gmail.com").build();
        EmployeeEntity m4=EmployeeEntity.builder().trigram("alx").email("alexis@gmail.com").build();
        employeeRepository.save(m3);
        employeeRepository.save(m4);
        deliverymen2.add(m3);
        deliverymen2.add(m4);
        //Creation delivery 3
        //creation order
        OrderEntity order31=OrderEntity.builder().reference("c31").build();
        OrderEntity order32=OrderEntity.builder().reference("c32").build();
        orderRepository.save(order31);
        orderRepository.save(order32);
        Set<OrderEntity> orders3 = new HashSet<>();
        orders3.add(order31);
        orders3.add(order32);
        DeliveryEntity del3=DeliveryEntity.builder().reference("T238G-B1").build();
        del3.setOrders(orders3);
        deliveryRepository.save(del3);
        //Creation delivery 4
        //creation order
        OrderEntity order41=OrderEntity.builder().reference("c41").build();
        OrderEntity order42=OrderEntity.builder().reference("c42").build();
        orderRepository.save(order41);
        orderRepository.save(order42);
        Set<OrderEntity> orders4 = new HashSet<>();
        orders4.add(order11);
        orders4.add(order12);
        DeliveryEntity del4=DeliveryEntity.builder().reference("T238G-B2").build();
        del4.setOrders(orders4);
        deliveryRepository.save(del4);
        Set<DeliveryEntity> deliveries2=new HashSet<>();
        deliveries2.add(del3);
        deliveries2.add(del4);
        //creation tour 2
        TruckEntity truck2=TruckEntity.builder().immatriculation("AB-345-CD").build();
        truckRepository.save(truck2);
        TourEntity tour2= TourEntity.builder().reference("T238G-B").build();
        tour2.setDeliverymen(deliverymen2);
        tour2.setTruck(truck2);
        tour2.setDeliveries(deliveries2);
        tourRepository.save(tour2);
        tours.add(tour2);

        // creation day
        DayEntity day = DayEntity.builder().reference("J238").date(LocalDate.of(2024,4,29)).build();
        day.setTours(tours);
        dayRepository.save(day);

        //when
        DayResponseDTO expectedResponse = dayService.getDay(LocalDate.of(2024,4,29));

        ResponseEntity<DayResponseDTO> response = testRestTemplate.exchange("/api/v2.0/planner/day?date={date}", HttpMethod.GET, new HttpEntity<>(null, headers), DayResponseDTO.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        AssertionsForClassTypes.assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(dayComponent, times(2)).getDay(any());
        verify(dayService, times(2)).getDay(any());

    }
    @Test
    void getDayNotFound() throws DayNotFoundException {
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("date", "2024-04-29");

        NotFoundErrorResponse expectedResponse = NotFoundErrorResponse.builder().uri("/api/v2.0/planner/day").errorMessage("No day found for the "+LocalDate.of(2024,4,29)).build();

        ResponseEntity<NotFoundErrorResponse> response = testRestTemplate.exchange("/api/v2.0/planner/day?date={date}", HttpMethod.GET, new HttpEntity<>(null, headers), NotFoundErrorResponse.class, urlParams);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedResponse);
        verify(dayComponent, times(1)).getDay(any());
        verify(dayService, times(1)).getDay(any());
    }


}
