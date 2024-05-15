package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.configuration.TokenRetriever;
import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.enums.DeliveryState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.TourState;
import fr.uga.l3miage.integrator.exceptions.NotFoundErrorResponse;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import fr.uga.l3miage.integrator.services.DeliveryService;
import fr.uga.l3miage.integrator.services.TourService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,properties = {"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect","spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true"})
@AutoConfigureTestDatabase
@AutoConfigureWebClient
public class DeliverymanControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @SpyBean
    private DeliveryService deliveryService;
    @SpyBean
    private TourService tourService;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private DayRepository dayRepository;
    @Autowired
    private WarehouseRepository warehouseRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @SpyBean
    private TourComponent tourComponent;

    private String token = TokenRetriever.getAccessToken("anaisanna@gmail.com", "123456");

    @BeforeEach
    public void setup() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        EmployeeEntity anais = EmployeeEntity.builder().email("anaisanna@gmail.com").trigram("aaa").photo(".png").job(Job.DELIVERYMAN).lastName("okj").firstName("jd").mobilePhone("098").build();
        employeeRepository.save(anais);
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
    void getTourOK() throws TourNotFoundException, DayNotFoundException {

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.token);

        //given
        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("email", "juju@gmail.com");

        //create and save a tour
        TourEntity tour = TourEntity.builder().reference("T123G-B").letter("G").distanceToCover(12.1).deliveries(new LinkedList<>()).build();
        EmployeeEntity man1 = EmployeeEntity.builder().email("antoinedupont@gmail.com").trigram("ant").photo(".png").lastName("okj").firstName("jd").mobilePhone("098Y5E").build();
        EmployeeEntity man2 = EmployeeEntity.builder().email("juju@gmail.com").trigram("jug").photo(".png").lastName("our").firstName("jug").mobilePhone("098YGED").build();
        WarehouseEntity warehouse =WarehouseEntity.builder().name("Grenis").letter("G").coordinates(new Coordinates(23.8,15.8765)).build();
        warehouseRepository.save(warehouse);
        man1.setWarehouse(warehouse);
        man2.setWarehouse(warehouse);
        employeeRepository.save(man1);
        employeeRepository.save(man2);

        TruckEntity truck = TruckEntity.builder().immatriculation("SE-544-TF").build();
        truckRepository.save(truck);

        Set<EmployeeEntity> deliverymen = new HashSet<>();
        deliverymen.add(man1);
        deliverymen.add(man2);
        tour.setDeliverymen(deliverymen);
        tour.setTruck(truck);
        Address customerAddress= Address.builder().address("02 rue des rois").city("paris").postalCode("75012").build();

        CustomerEntity customer=CustomerEntity.builder().lastName("axel").firstName("leroy").email("axel@gmail.com").address(customerAddress).build();
        customerRepository.save(customer);
        OrderEntity order1=OrderEntity.builder().reference("c23").customer(customer).build();
        orderRepository.save(order1);

        DeliveryEntity d1=DeliveryEntity.builder().reference("L1").orders(Set.of(order1)).coordinates(new Coordinates()).build();
        deliveryRepository.save(d1);
        List<DeliveryEntity> s1= new LinkedList<>();
        s1.add(d1);
        tour.setDeliveries(s1);
        tourRepository.save(tour);

        //create and save a planned day including the above tour
        LocalDate today = LocalDate.now();
        DayEntity day = DayEntity.builder().reference("J123G").date(today).build();
        List<TourEntity> tours = new LinkedList<>();
        tours.add(tour);
        day.setTours(tours);
        dayRepository.save(day);


        //when
        TourDMResponseDTO expectedResponse = tourService.getDeliveryTourOfTheDay(man2.getEmail());

        ResponseEntity<TourDMResponseDTO> response = testRestTemplate.exchange("/api/v3.0/deliveryman/tour?email={email}", HttpMethod.GET, new HttpEntity<>(null, headers), TourDMResponseDTO.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(tourComponent, times(2)).getTourOfTheDay(anyString());
        verify(tourService, times(2)).getDeliveryTourOfTheDay(anyString());


    }


    @Test
    void getTourNotFoundBecauseOfNotFoundDay() throws TourNotFoundException, DayNotFoundException {

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.token);

        //given
        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("email", "hola@gmail.com");

        //when
        NotFoundErrorResponse expectedResponse = NotFoundErrorResponse.builder().uri("/api/v3.0/deliveryman/tour").errorMessage("No day was planned for today : "+LocalDate.now()).build();

        ResponseEntity<NotFoundErrorResponse> response = testRestTemplate.exchange("/api/v3.0/deliveryman/tour?email={email}", HttpMethod.GET, new HttpEntity<>(null, headers), NotFoundErrorResponse.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedResponse);
        verify(tourComponent, times(1)).getTourOfTheDay(anyString());
        verify(tourService, times(1)).getDeliveryTourOfTheDay(anyString());


    }

    @Test
    void getTourNotFoundBecauseOfNotFoundDeliveryman() throws TourNotFoundException, DayNotFoundException {

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.token);

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("email", "hola@gmail.com");

        //create and save a planned day including the above tour
        DayEntity day = DayEntity.builder().reference("J123G").build();
        day.setDate(LocalDate.now());
        dayRepository.save(day);

        //when
        NotFoundErrorResponse expectedResponse = NotFoundErrorResponse.builder().uri("/api/v3.0/deliveryman/tour").errorMessage("No tour was found for <hola@gmail.com>").build();
        ResponseEntity<NotFoundErrorResponse> response = testRestTemplate.exchange("/api/v3.0/deliveryman/tour?email={email}", HttpMethod.GET, new HttpEntity<>(null, headers), NotFoundErrorResponse.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedResponse);
        verify(tourComponent, times(1)).getTourOfTheDay(anyString());
        verify(tourService, times(1)).getDeliveryTourOfTheDay(anyString());


    }

    @Test
    void updateDeliveryStateOK(){
        //given

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.token);

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("deliveryId", "l130G-A1");
        urlParams.put("deliveryState","UNLOADING");
        urlParams.put("tourId","t130G-A");

        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l130G-A1")
                .state(DeliveryState.IN_COURSE)
                .distanceToCover(3.9)
                .orders(Set.of())
                .coordinates(new Coordinates())
                .build();
        deliveryRepository.save(deliveryEntity);
        TourEntity tour=TourEntity.builder().reference("t130G-A").deliveries(List.of(deliveryEntity)).build();
        tourRepository.save(tour);

        //when
        ResponseEntity<Void> response=testRestTemplate.exchange("/api/v3.0/deliveryman/tours/{tourId}/deliveries/{deliveryId}/updateState?deliveryState={deliveryState}", HttpMethod.PATCH, new HttpEntity<>(null, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(deliveryService,times(1)).updateDeliveryState(DeliveryState.UNLOADING,deliveryEntity.getReference(),tour.getReference());


    }

    @Test
    void updateDeliveryStateAndTourOK() throws TourNotFoundException {
        //given

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + TokenRetriever.getAccessToken("anaisanna@gmail.com", "123456"));

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("deliveryId", "l130G-A1");
        urlParams.put("deliveryState","COMPLETED");
        urlParams.put("tourId","t130G-A");

        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l130G-A1")
                .state(DeliveryState.WITH_CUSTOMER)
                .distanceToCover(3.9)
                .orders(Set.of())
                .coordinates(new Coordinates())
                .build();
        deliveryRepository.save(deliveryEntity);
        TourEntity tour=TourEntity.builder().state(TourState.IN_COURSE).reference("t130G-A").deliveries(List.of(deliveryEntity)).build();
        tourRepository.save(tour);

        //when
        ResponseEntity<Void> response=testRestTemplate.exchange("/api/v3.0/deliveryman/tours/{tourId}/deliveries/{deliveryId}/updateState?deliveryState={deliveryState}", HttpMethod.PATCH, new HttpEntity<>(null, headers), Void.class, urlParams);

        TourEntity tourAfterUpdating= tourRepository.findById(tour.getReference()).orElseThrow(()-> new TourNotFoundException("No tour was found "));
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(deliveryService,times(1)).updateDeliveryState(DeliveryState.COMPLETED,deliveryEntity.getReference(),tour.getReference());
        assertThat(tourAfterUpdating.getState()).isEqualTo(TourState.COMPLETED);


    }

    @Test
    void updateDeliveryStateNotOK_BecauseOfNotFoundDelivery(){
        //given

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.token);

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("deliveryId", "l130G-A1");
        urlParams.put("deliveryState","UNLOADING");
        urlParams.put("tourId","t130G-A");

        //when
        ResponseEntity<Void> response=testRestTemplate.exchange("/api/v3.0/deliveryman/tours/{tourId}/deliveries/{deliveryId}/updateState?deliveryState={deliveryState}", HttpMethod.PATCH, new HttpEntity<>(null, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(deliveryService,times(1)).updateDeliveryState(DeliveryState.UNLOADING,"l130G-A1","t130G-A");


    }

    @Test
    void updateDeliveryStateNotOK_BecauseOfWrongState(){
        //given
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.token);

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("deliveryId", "l130G-A1");
        urlParams.put("deliveryState","COMPLETED");
        urlParams.put("tourId","t130G-A");

        DeliveryEntity deliveryEntity=DeliveryEntity.builder()
                .reference("l130G-A1")
                .state(DeliveryState.IN_COURSE)
                .distanceToCover(3.9)
                .orders(Set.of())
                .coordinates(new Coordinates())
                .build();
        deliveryRepository.save(deliveryEntity);
        TourEntity tour = TourEntity.builder().reference("t130G-A").deliveries(List.of(deliveryEntity)).build();
        tourRepository.save(tour);


        //when
        ResponseEntity<Void> response=testRestTemplate.exchange("/api/v3.0/deliveryman/tours/{tourId}/deliveries/{deliveryId}/updateState?deliveryState={deliveryState}", HttpMethod.PATCH, new HttpEntity<>(null, headers), Void.class, urlParams);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(deliveryService,times(1)).updateDeliveryState(DeliveryState.COMPLETED,deliveryEntity.getReference(),tour.getReference());


    }

    @Test
    void updateTourInCourseOK() throws TourNotFoundException {
        //given
        final HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + this.token);

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("deliveryId", "l130G-A2");
        urlParams.put("deliveryState", "IN_COURSE");
        urlParams.put("tourId", "t130G-A");

        DeliveryEntity deliveryEntity = DeliveryEntity.builder()
                .reference("l130G-A1")
                .state(DeliveryState.COMPLETED)
                .distanceToCover(3.9)
                .orders(Set.of())
                .coordinates(new Coordinates())
                .build();
        deliveryRepository.save(deliveryEntity);

        DeliveryEntity deliveryEntity1 = DeliveryEntity.builder()
                .reference("l130G-A2")
                .state(DeliveryState.PLANNED)
                .distanceToCover(3.9)
                .orders(Set.of())
                .coordinates(new Coordinates())
                .build();
        deliveryRepository.save(deliveryEntity1);

        TourEntity tour = TourEntity.builder().state(TourState.PLANNED).reference("t130G-A").deliveries(List.of(deliveryEntity,deliveryEntity1)).build();
        tourRepository.save(tour);

        //when
        ResponseEntity<Void> response = testRestTemplate.exchange("/api/v3.0/deliveryman/tours/{tourId}/deliveries/{deliveryId}/updateState?deliveryState={deliveryState}", HttpMethod.PATCH, new HttpEntity<>(null, headers), Void.class, urlParams);

        TourEntity tourAfterUpdating = tourRepository.findById(tour.getReference()).orElseThrow(() -> new TourNotFoundException("No tour was found "));
        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(deliveryService, times(1)).updateDeliveryState(DeliveryState.IN_COURSE, deliveryEntity1.getReference(), tour.getReference());
        assertThat(tourAfterUpdating.getState()).isEqualTo(TourState.IN_COURSE);
    }


}
