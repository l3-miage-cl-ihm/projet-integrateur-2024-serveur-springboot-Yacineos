package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.components.DayComponent;
import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.enums.*;
import fr.uga.l3miage.integrator.exceptions.NotFoundErrorResponse;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import fr.uga.l3miage.integrator.services.DayService;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.time.LocalDate;
import java.util.*;

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

    @BeforeEach
    public void setup() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }
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
                .address(new Address("21 rue des cafards", "65001", "San antonio")).trucks(Set.of()).coordinates(new Coordinates(23.74,54.84)).build();
        warehouseRepository.save(grenis);
        EmployeeEntity planner = EmployeeEntity.builder().email("claudiatessiere@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("TESSIERE").firstName("claudia").mobilePhone("0765437876").trigram("STR").warehouse(grenis).build();
        employeeRepository.save(planner);

        List<TourCreationRequest> tourCreationRequestList= new ArrayList<>();
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
                .warehouse(grenis)
                .build();
        EmployeeEntity deliveryman2= EmployeeEntity.builder()
                .email("jugurta@gmail.com")
                .job(Job.DELIVERYMAN)
                .trigram("JOK")
                .firstName("Jugurta")
                .lastName("Ourzik")
                .mobilePhone("065432354")
                .photo("juju.png")
                .warehouse(grenis)
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
        List<DeliveryCreationRequest> deliveryCreationRequestList=new ArrayList<>();
        DeliveryCreationRequest d1= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o1.getReference(),o2.getReference()))
                .coordinates(List.of(12.7,34.8))
                .build();

        DeliveryCreationRequest d2= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o3.getReference()))
                .coordinates(List.of(12.7,34.8))
                .build();
        deliveryCreationRequestList.add(d1);
        deliveryCreationRequestList.add(d2);
        TourCreationRequest t1= TourCreationRequest.builder()
                .distanceToCover(24)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(),deliveryman2.getTrigram()))
                .deliveries(deliveryCreationRequestList)
                .build();

        tourCreationRequestList.add(t1);
        DayCreationRequest dayCreationRequest= DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(tourCreationRequestList)
                .build();


        //when
        ResponseEntity<Void> response=testRestTemplate.exchange("/api/v3.0/planner/day/plan", HttpMethod.POST, new HttpEntity<>(dayCreationRequest, headers), Void.class, urlParams);

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
                    .tours(new ArrayList<>())
                    .build();


            //when
            ResponseEntity<Void> response=testRestTemplate.exchange("/api/v3.0/planner/day/plan", HttpMethod.POST, new HttpEntity<>(dayCreationRequest, headers), Void.class, urlParams);

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
                .address(new Address("21 rue des cafards", "65001", "San antonio")).trucks(Set.of()) .coordinates(new Coordinates(12.65,65.86201)).build();
        warehouseRepository.save(grenis);
        EmployeeEntity planner = EmployeeEntity.builder().email("claudiatessiere@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("TESSIERE").firstName("claudia").mobilePhone("0765437876").trigram("STR").warehouse(grenis).build();
        employeeRepository.save(planner);

        List<TourCreationRequest> tourCreationRequestList= new ArrayList<>();
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
                .warehouse(grenis)
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
        List<DeliveryCreationRequest> deliveryCreationRequestList = new ArrayList<>();
        DeliveryCreationRequest d1 = DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o1.getReference(), o2.getReference()))
                .build();

        DeliveryCreationRequest d2 = DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o3.getReference()))
                .build();
        deliveryCreationRequestList.add(d1);
        deliveryCreationRequestList.add(d2);
        TourCreationRequest t1 = TourCreationRequest.builder()
                .distanceToCover(24)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(), "YTR")) //no existing deliveryman with trigram 'YTR'
                .deliveries(deliveryCreationRequestList)
                .build();

        tourCreationRequestList.add(t1);
        DayCreationRequest dayCreationRequest = DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(tourCreationRequestList)
                .build();


        //when
        //dayService.planDay(dayCreationRequest);  //Here dayService returns nothing but the call can help to verify others repositories, components or mappers apparences.
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v3.0/planner/day/plan", HttpMethod.POST, new HttpEntity<>(dayCreationRequest, headers), Void.class, urlParams);

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
                .address(new Address("21 rue des cafards", "65001", "San antonio")).coordinates(new Coordinates(12.65,65.86201)).trucks(Set.of()).build();
        warehouseRepository.save(grenis);
        EmployeeEntity planner = EmployeeEntity.builder().email("christian.paul@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("Paul").firstName("Christian").mobilePhone("0765437876").trigram("STR").warehouse(grenis).build();
        employeeRepository.save(planner);

        List<TourCreationRequest> tourCreationRequestList = new ArrayList<>();
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
                .warehouse(grenis)
                .build();
        EmployeeEntity deliveryman2= EmployeeEntity.builder()
                .email("jugurta@gmail.com")
                .job(Job.DELIVERYMAN)
                .trigram("JOK")
                .firstName("Jugurta")
                .lastName("Ourzik")
                .warehouse(grenis)
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
        List<DeliveryCreationRequest> deliveryCreationRequestList = new ArrayList<>();
        DeliveryCreationRequest d1 = DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o1.getReference(), o2.getReference()))
                .coordinates(List.of(12.6,12.7))
                .build();

        DeliveryCreationRequest d2 = DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of("c900")) //no existing order with given reference c170
                .coordinates(List.of(12.6,12.7))
                .build();
        deliveryCreationRequestList.add(d1);
        deliveryCreationRequestList.add(d2);
        TourCreationRequest t1 = TourCreationRequest.builder()
                .distanceToCover(24)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(), deliveryman2.getTrigram()))
                .deliveries(deliveryCreationRequestList)
                .build();

        tourCreationRequestList.add(t1);
        DayCreationRequest dayCreationRequest = DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(tourCreationRequestList)
                .build();


        //when
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v3.0/planner/day/plan", HttpMethod.POST, new HttpEntity<>(dayCreationRequest, headers), Void.class, urlParams);

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
        List<TourEntity> tours= new ArrayList<>();
        Set<EmployeeEntity> deliverymen= new HashSet<>();
        WarehouseEntity grenis = WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards", "65001", "San antonio")).coordinates(new Coordinates(12.65,65.86201)).trucks(Set.of()).build();
        warehouseRepository.save(grenis);
        EmployeeEntity m1=EmployeeEntity.builder().trigram("jjo").warehouse(grenis).email("jojo@gmail.com").build();
        EmployeeEntity m2=EmployeeEntity.builder().trigram("axl").warehouse(grenis).email("axel@gmail.com").build();
        employeeRepository.save(m1);
        employeeRepository.save(m2);
        deliverymen.add(m1);
        deliverymen.add(m2);
        //Creation delivery 1
        //creation order
        //creation customers
        CustomerEntity customer1= CustomerEntity.builder().address(new Address("1 rue de paris","75000","Paris")).state(CustomerState.REGISTERED).firstName("A").lastName("E").email("A@gmail.com").build();
        CustomerEntity customer2= CustomerEntity.builder().address(new Address("2 rue de marseille","13000","Marseille")).state(CustomerState.REGISTERED).firstName("B").lastName("F").email("B@gmail.com").build();
        CustomerEntity customer3= CustomerEntity.builder().address(new Address("3 rue de valence","26000","Valence")).state(CustomerState.REGISTERED).firstName("C").lastName("G").email("C@gmail.com").build();
        CustomerEntity customer4= CustomerEntity.builder().address(new Address("4 rue de toulon","10000","Toulon")).state(CustomerState.REGISTERED).firstName("D").lastName("H").email("D@gmail.com").build();
        customerRepository.save(customer1);
        customerRepository.save(customer2);
        customerRepository.save(customer3);
        customerRepository.save(customer4);

        OrderEntity order11=OrderEntity.builder().reference("c11").customer(customer1).build();
        OrderEntity order12=OrderEntity.builder().reference("c12").customer(customer1).build();
        orderRepository.save(order11);
        orderRepository.save(order12);
        DeliveryEntity del1=DeliveryEntity.builder().reference("T238G-A1").coordinates(new Coordinates(12.5,34.8)).build();
        del1.setOrders(Set.of(order11,order12));
        deliveryRepository.save(del1);
        //Creation delivery
        //creation order
        OrderEntity order21=OrderEntity.builder().reference("c21").customer(customer2).build();
        OrderEntity order22=OrderEntity.builder().reference("c22").customer(customer2).build();
        orderRepository.save(order21);
        orderRepository.save(order22);
        DeliveryEntity del2=DeliveryEntity.builder().reference("T238G-A2").coordinates(new Coordinates(12.5,34.8)).build();
        del2.setOrders(Set.of(order21,order22));
        deliveryRepository.save(del2);
        List<DeliveryEntity> deliveries1=new ArrayList<>();
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
        EmployeeEntity m3=EmployeeEntity.builder().trigram("jju").warehouse(grenis).email("juju@gmail.com").build();
        EmployeeEntity m4=EmployeeEntity.builder().trigram("alx").warehouse(grenis).email("alexis@gmail.com").build();
        employeeRepository.save(m3);
        employeeRepository.save(m4);
        deliverymen2.add(m3);
        deliverymen2.add(m4);
        //Creation delivery 3
        //creation order
        OrderEntity order31=OrderEntity.builder().reference("c31").customer(customer3).build();
        OrderEntity order32=OrderEntity.builder().reference("c32").customer(customer3).build();
        orderRepository.save(order31);
        orderRepository.save(order32);
        DeliveryEntity del3=DeliveryEntity.builder().coordinates(new Coordinates(12.5,34.8)).reference("T238G-B1").build();
        del3.setOrders(Set.of(order31,order32));
        deliveryRepository.save(del3);
        //Creation delivery 4
        //creation order
        OrderEntity order41=OrderEntity.builder().reference("c41").customer(customer4).build();
        OrderEntity order42=OrderEntity.builder().reference("c42").customer(customer4).build();
        orderRepository.save(order41);
        orderRepository.save(order42);

        DeliveryEntity del4=DeliveryEntity.builder().reference("T238G-B2").orders(Set.of(order41,order42)).coordinates(new Coordinates(12.5,34.8)).build();
        deliveryRepository.save(del4);
        List<DeliveryEntity> deliveries2=new LinkedList<>();
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
        ResponseEntity<DayResponseDTO> response = testRestTemplate.exchange("/api/v3.0/planner/day?date={date}", HttpMethod.GET, new HttpEntity<>(null, headers), DayResponseDTO.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(dayComponent, times(2)).getDay(any());
        verify(dayService, times(2)).getDay(any());

    }
    @Test
    void getDayNotFound() throws DayNotFoundException {
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("date", "2024-04-29");

        NotFoundErrorResponse expectedResponse = NotFoundErrorResponse.builder().uri("/api/v3.0/planner/day").errorMessage("No day found for the "+LocalDate.of(2024,4,29)).build();

        ResponseEntity<NotFoundErrorResponse> response = testRestTemplate.exchange("/api/v3.0/planner/day?date={date}", HttpMethod.GET, new HttpEntity<>(null, headers), NotFoundErrorResponse.class, urlParams);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedResponse);
        verify(dayComponent, times(1)).getDay(any());
        verify(dayService, times(1)).getDay(any());
    }

    @Test
    void getSetUpBundle() {

        final HttpHeaders headers = new HttpHeaders();
        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("warehouseId", "Grenis");

        Address a1 = new Address("21 rue de la paix", "38000", "Grenoble");
        Address a2 = new Address("azeazeazed", "38000", "Grenoble");
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
                .creationDate(LocalDate.of(2022, 1, 9))
                .customer(c1)
                .state(OrderState.OPENED)
                .build();
        orderRepository.save(o2);
        OrderEntity o3 = OrderEntity.builder()
                .reference("c03")
                .creationDate(LocalDate.of(2023, 1, 8))
                .customer(c2)
                .state(OrderState.OPENED)
                .build();
        orderRepository.save(o3);

        TruckEntity t1 = TruckEntity.builder()
                .immatriculation("ABC")
                .build();
        TruckEntity t2 = TruckEntity.builder()
                .immatriculation("DEF")
                .build();
        truckRepository.save(t1);
        truckRepository.save(t2);

        WarehouseEntity warehouse=WarehouseEntity.builder()
                .name("Grenis")
                .photo(".png")
                .coordinates(new Coordinates(23.87,30.442))
                .address(new Address("22 rue des rats","75033","Nice"))
                .days(Set.of())
                .trucks(Set.of(t1,t2))
                .letter("G")
                .build();

        warehouseRepository.save(warehouse);

        EmployeeEntity e1 = EmployeeEntity.builder()
                .job(Job.DELIVERYMAN)
                .trigram("abc")
                .warehouse(warehouse)
                .build();
        EmployeeEntity e2 = EmployeeEntity.builder()
                .job(Job.DELIVERYMAN)
                .trigram("def")
                .warehouse(warehouse)
                .build();
        EmployeeEntity e3 = EmployeeEntity.builder()
                .job(Job.DELIVERYMAN)
                .trigram("ghi")
                .warehouse(warehouse)
                .build();

        employeeRepository.save(e1);
        employeeRepository.save(e2);
        employeeRepository.save(e3);

        SetUpBundleResponse expectedResponse = dayService.getSetUpBundle(warehouse.getName());
        ResponseEntity<SetUpBundleResponse> response = testRestTemplate.exchange("/api/v3.0/planner/{warehouseId}/bundle", HttpMethod.GET, new HttpEntity<>(null, headers), SetUpBundleResponse.class,urlParams);
        AssertionsForClassTypes.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(dayService, times(2)).getSetUpBundle(warehouse.getName());
    }


    @Test
    void editDayOK(){
        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("dayId","J131G");

        //IMPORTANT: do not forget the planner because as decided before, he is one to manage the warehouse and it is loaded in the DayPlannerMapper
        WarehouseEntity grenis = WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards", "65001", "San antonio")).trucks(Set.of())
                .coordinates(new Coordinates(12.65,65.86201))
                .build();
        warehouseRepository.save(grenis);
        EmployeeEntity planner = EmployeeEntity.builder().email("claudiatessiere@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("TESSIERE").firstName("claudia").mobilePhone("0765437876").trigram("STR").warehouse(grenis).build();
        employeeRepository.save(planner);

        List<TourCreationRequest> tourCreationRequestList= new ArrayList<>();
        TruckEntity truck= TruckEntity.builder().immatriculation("AY-124-GF").build();
        truckRepository.save(truck);
        EmployeeEntity deliveryman1= EmployeeEntity.builder()
                .email("samy@gmail.com")
                .job(Job.DELIVERYMAN)
                .trigram("SSA")
                .firstName("Samy")
                .lastName("Silva")
                .warehouse(grenis)
                .mobilePhone("0654326754")
                .photo("samy.png")
                .build();
        EmployeeEntity deliveryman2= EmployeeEntity.builder()
                .email("jugurta@gmail.com")
                .job(Job.DELIVERYMAN)
                .trigram("JOK")
                .warehouse(grenis)
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
        List<DeliveryCreationRequest> deliveryCreationRequestList=new ArrayList<>();
        DeliveryCreationRequest d1= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o1.getReference()))
                .coordinates(List.of(12.6,12.7))
                .build();

        DeliveryCreationRequest d2= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o3.getReference()))
                .coordinates(List.of(12.6,12.7))
                .build();
        deliveryCreationRequestList.add(d1);
        deliveryCreationRequestList.add(d2);
        TourCreationRequest t1= TourCreationRequest.builder()
                .distanceToCover(24)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(),deliveryman2.getTrigram()))
                .deliveries(deliveryCreationRequestList)
                .build();

        tourCreationRequestList.add(t1);
        LocalDate date= LocalDate.of(2024,05,10);

        DayCreationRequest editDayRequest= DayCreationRequest.builder()
                .date(date)
                .tours(tourCreationRequestList)
                .build();


        //*************
        DeliveryEntity delivery1=DeliveryEntity.builder()
                .reference("J131G-A1")
                .orders(Set.of(o1,o2))
                .state(DeliveryState.PLANNED)
                .coordinates(new Coordinates(12.4,23))
                .build();
        DeliveryEntity delivery2=DeliveryEntity.builder()
                .reference("J131G-A2")
                .orders(Set.of(o3))
                .state(DeliveryState.PLANNED)
                .coordinates(new Coordinates(12.4,23))
                .build();
        deliveryRepository.save(delivery1);
        deliveryRepository.save(delivery2);
        TourEntity tour1=TourEntity.builder()
                .reference("J131G-A")
                .state(TourState.PLANNED)
                .truck(truck)
                .deliveries(List.of(delivery1,delivery2))
                .build();
        tourRepository.save(tour1);
        List<TourEntity> tours=new ArrayList<>();
        tours.add(tour1);
        DayEntity day=DayEntity.builder().date(date).reference("J131G")
                .planner(planner)
                .state(DayState.PLANNED)
                .tours(tours)
                .build();
        dayRepository.save(day);

        //*************

        //when
        ResponseEntity<Void> response=testRestTemplate.exchange("/api/v3.0/planner/days/{dayId}/edit", HttpMethod.PUT, new HttpEntity<>(editDayRequest, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(dayService,times(1)).editDay(editDayRequest,day.getReference());



    }

    @Test  //simulating day plannification without any tour
    void editDay_NotOK_BecauseOfMissedInputs(){
        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("dayId","J131G");

        WarehouseEntity grenis = WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards", "65001", "San antonio")).trucks(Set.of()) .coordinates(new Coordinates(12.65,65.86201)).build();
        warehouseRepository.save(grenis);
        EmployeeEntity planner = EmployeeEntity.builder().email("claudiatessiere@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("TESSIERE").firstName("claudia").mobilePhone("0765437876").trigram("STR").warehouse(grenis).build();
        employeeRepository.save(planner);

        List<TourCreationRequest> tourCreationRequestList= new ArrayList<>();
        TruckEntity truck= TruckEntity.builder().immatriculation("AY-124-GF").build();
        truckRepository.save(truck);
        EmployeeEntity deliveryman1= EmployeeEntity.builder()
                .email("samy@gmail.com")
                .job(Job.DELIVERYMAN)
                .trigram("SSA")
                .firstName("Samy")
                .lastName("Silva")
                .warehouse(grenis)
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
                .warehouse(grenis)
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




        //*************
        DeliveryEntity delivery1=DeliveryEntity.builder()
                .reference("J131G-A1")
                .orders(Set.of(o1,o2))
                .state(DeliveryState.PLANNED)
                .coordinates(new Coordinates(12.4,12.876))
                .build();
        DeliveryEntity delivery2=DeliveryEntity.builder()
                .reference("J131G-A2")
                .orders(Set.of(o3))
                .coordinates(new Coordinates(12.4,12.876))
                .state(DeliveryState.PLANNED)
                .build();
        deliveryRepository.save(delivery1);
        deliveryRepository.save(delivery2);
        TourEntity tour1=TourEntity.builder()
                .reference("J131G-A")
                .state(TourState.PLANNED)
                .truck(truck)
                .deliveries(List.of(delivery1,delivery2))
                .build();
        tourRepository.save(tour1);
        List<TourEntity> tours=new ArrayList<>();
        tours.add(tour1);
        DayEntity day=DayEntity.builder().date(LocalDate.of(2024,05,10)).reference("J131G")
                .planner(planner)
                .state(DayState.PLANNED)
                .tours(tours)
                .build();
        dayRepository.save(day);

        DayCreationRequest editDayRequest= DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(new ArrayList<>())
                .build();


        //when
        ResponseEntity<Void> response=testRestTemplate.exchange("/api/v3.0/planner/days/{dayId}/edit", HttpMethod.PUT, new HttpEntity<>(editDayRequest, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        verify(dayService,times(1)).editDay(editDayRequest,day.getReference());



    }

    @Test  //simulating wrong deliveryman trigram input
    void editDay_NotOK_BecauseOfWrongInputs1() {

        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("dayId","J131G");

        //IMPORTANT: do not forget the planner because as decided before, he is one to manage the warehouse and it is loaded in the DayPlannerMapper
        WarehouseEntity grenis = WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G") .coordinates(new Coordinates(12.65,65.86201))
                .address(new Address("21 rue des cafards", "65001", "San antonio")).trucks(Set.of()).build();
        warehouseRepository.save(grenis);
        EmployeeEntity planner = EmployeeEntity.builder().email("claudiatessiere@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("TESSIERE").firstName("claudia").mobilePhone("0765437876").trigram("STR").warehouse(grenis).build();
        employeeRepository.save(planner);

        List<TourCreationRequest> tourCreationRequestList= new ArrayList<>();
        TruckEntity truck = TruckEntity.builder().immatriculation("AY-124-GF").build();
        truckRepository.save(truck);
        EmployeeEntity deliveryman1 = EmployeeEntity.builder()
                .email("samy@gmail.com")
                .job(Job.DELIVERYMAN)
                .trigram("SSA")
                .firstName("Samy")
                .lastName("Silva")
                .warehouse(grenis)
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
        List<DeliveryCreationRequest> deliveryCreationRequestList = new ArrayList<>();
        DeliveryCreationRequest d1 = DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o1.getReference(), o2.getReference()))
                .coordinates(List.of(12.6,123.34))
                .build();

        DeliveryCreationRequest d2 = DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o3.getReference()))
                .coordinates(List.of(12.6,123.34))
                .build();
        deliveryCreationRequestList.add(d1);
        deliveryCreationRequestList.add(d2);
        TourCreationRequest t1 = TourCreationRequest.builder()
                .distanceToCover(24)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(), "YTR")) //no existing deliveryman with trigram 'YTR'
                .deliveries(deliveryCreationRequestList)
                .build();

        tourCreationRequestList.add(t1);
        DayCreationRequest editDayRequest = DayCreationRequest.builder()
                .date(LocalDate.of(2024,05,10))
                .tours(tourCreationRequestList)
                .build();

        //*************
        DeliveryEntity delivery1=DeliveryEntity.builder()
                .reference("J131G-A1")
                .orders(Set.of(o1,o2))
                .state(DeliveryState.PLANNED)
                .coordinates(new Coordinates(12.4,12.876))
                .build();
        DeliveryEntity delivery2=DeliveryEntity.builder()
                .reference("J131G-A2")
                .orders(Set.of(o3))
                .state(DeliveryState.PLANNED)
                .coordinates(new Coordinates(12.4,12.876))
                .build();
        deliveryRepository.save(delivery1);
        deliveryRepository.save(delivery2);
        TourEntity tour1=TourEntity.builder()
                .reference("J131G-A")
                .state(TourState.PLANNED)
                .truck(truck)
                .deliveries(List.of(delivery1,delivery2))
                .build();
        tourRepository.save(tour1);
        List<TourEntity> tours=new ArrayList<>();
        tours.add(tour1);
        DayEntity day=DayEntity.builder().date(LocalDate.of(2024,05,10)).reference("J131G")
                .planner(planner)
                .state(DayState.PLANNED)
                .tours(tours)
                .build();
        dayRepository.save(day);

        //when
        //dayService.planDay(editDayRequest);  //Here dayService returns nothing but the call can help to verify others repositories, components or mappers apparences.
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v3.0/planner/days/{dayId}/edit", HttpMethod.PUT, new HttpEntity<>(editDayRequest, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);
        verify(dayService, times(1)).editDay(editDayRequest,day.getReference());

    }

    @Test  //simulating not found day
    void editDay_NotOK_BecauseOfNotFoundDay() {

        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("dayId","J131G");

        //IMPORTANT: do not forget the planner because as decided before, he is one to manage the warehouse and it is loaded in the DayPlannerMapper
        WarehouseEntity grenis = WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards", "65001", "San antonio")) .coordinates(new Coordinates(12.65,65.86201)).trucks(Set.of()).build();
        warehouseRepository.save(grenis);
        EmployeeEntity planner = EmployeeEntity.builder().email("claudiatessiere@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("TESSIERE").firstName("claudia").mobilePhone("0765437876").trigram("STR").warehouse(grenis).build();
        employeeRepository.save(planner);

        List<TourCreationRequest> tourCreationRequestList= new ArrayList<>();
        TruckEntity truck = TruckEntity.builder().immatriculation("AY-124-GF").build();
        truckRepository.save(truck);
        EmployeeEntity deliveryman1 = EmployeeEntity.builder()
                .email("samy@gmail.com")
                .job(Job.DELIVERYMAN)
                .trigram("SSA")
                .firstName("Samy")
                .lastName("Silva")
                .warehouse(grenis)
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
        List<DeliveryCreationRequest> deliveryCreationRequestList = new ArrayList<>();
        DeliveryCreationRequest d1 = DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o1.getReference(), o2.getReference()))
                .build();

        DeliveryCreationRequest d2 = DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o3.getReference()))
                .build();
        deliveryCreationRequestList.add(d1);
        deliveryCreationRequestList.add(d2);
        TourCreationRequest t1 = TourCreationRequest.builder()
                .distanceToCover(24)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(), "YTR")) //no existing deliveryman with trigram 'YTR'
                .deliveries(deliveryCreationRequestList)
                .build();

        tourCreationRequestList.add(t1);
        DayCreationRequest editDayRequest = DayCreationRequest.builder()
                .date(LocalDate.of(2024,05,10))
                .tours(tourCreationRequestList)
                .build();

        //when
        //dayService.planDay(editDayRequest);  //Here dayService returns nothing but the call can help to verify others repositories, components or mappers apparences.
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v3.0/planner/days/{dayId}/edit", HttpMethod.PUT, new HttpEntity<>(editDayRequest, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(dayService, times(1)).editDay(editDayRequest,"J131G");

    }


    @Test
    void updateDayStateOK1(){
        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("dayId","J131G");
        urlParams.put("newDayState","IN_PROGRESS");

        DayEntity day= DayEntity.builder()
                .state(DayState.PLANNED)
                .reference("J131G")
                .build();
       dayRepository.save(day);


        //when
        ResponseEntity<Void> response=testRestTemplate.exchange("/api/v3.0/planner/days/{dayId}/updateState?newDayState={newDayState}", HttpMethod.PATCH, new HttpEntity<>(null, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(dayService,times(1)).updateDayState(day.getReference(),DayState.IN_PROGRESS);

    }

    @Test
    void updateDayStateOK2() {
        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("dayId", "J131G");
        urlParams.put("newDayState", "COMPLETED");

        TourEntity tour=TourEntity.builder()
                .reference("J131G-A")
                .state(TourState.COMPLETED)
                .build();
        tourRepository.save(tour);
        DayEntity day = DayEntity.builder()
                .state(DayState.IN_PROGRESS)
                .tours(List.of(tour))
                .reference("J131G")
                .build();
        dayRepository.save(day);


        //when
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v3.0/planner/days/{dayId}/updateState?newDayState={newDayState}", HttpMethod.PATCH, new HttpEntity<>(null, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(dayService, times(1)).updateDayState(day.getReference(), DayState.COMPLETED);
    }

    @Test
    void updateDayStateNotOK_BecauseOf_TourState() {
        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("dayId", "J131G");
        urlParams.put("newDayState", "COMPLETED");

        TourEntity tour=TourEntity.builder()
                .reference("J131G-A")
                .state(TourState.CUSTOMER)
                .build();
        tourRepository.save(tour);
        DayEntity day = DayEntity.builder()
                .state(DayState.IN_PROGRESS)
                .tours(List.of(tour))
                .reference("J131G")
                .build();
        dayRepository.save(day);


        //when
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v3.0/planner/days/{dayId}/updateState?newDayState={newDayState}", HttpMethod.PATCH, new HttpEntity<>(null, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(dayService, times(1)).updateDayState(day.getReference(), DayState.COMPLETED);
    }

    @Test
    void updateDayStateNotOK_BecauseOf_NotFoundDay() {
        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("dayId", "J131G");
        urlParams.put("newDayState", "COMPLETED");

        //when
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v3.0/planner/days/{dayId}/updateState?newDayState={newDayState}", HttpMethod.PATCH, new HttpEntity<>(null, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(dayService, times(1)).updateDayState("J131G", DayState.COMPLETED);
    }

    @Test
    void updateDayStateNotOK_BecauseOfWrongState() {
        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("dayId", "J131G");
        urlParams.put("newDayState", "COMPLETED");
        DayEntity day = DayEntity.builder()
                .state(DayState.COMPLETED)
                .reference("J131G")
                .build();
        dayRepository.save(day);


        //when
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v3.0/planner/days/{dayId}/updateState?newDayState={newDayState}", HttpMethod.PATCH, new HttpEntity<>(null, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(dayService, times(1)).updateDayState(day.getReference(), DayState.COMPLETED);
    }

}
