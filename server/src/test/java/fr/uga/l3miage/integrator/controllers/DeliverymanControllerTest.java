package fr.uga.l3miage.integrator.controllers;


import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.dataTypes.Address;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import fr.uga.l3miage.integrator.services.TourService;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
@AutoConfigureTestDatabase
@AutoConfigureWebClient
public class DeliverymanControllerTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
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
    @AfterEach
    public void clear() {
        tourRepository.deleteAll();
        dayRepository.deleteAll();
        customerRepository.deleteAll();
        orderRepository.deleteAll();
        deliveryRepository.deleteAll();
        employeeRepository.deleteAll();
        truckRepository.deleteAll();
        warehouseRepository.deleteAll();

    }


    @Transactional
    @Test
    void getTourOK() throws TourNotFoundException, DayNotFoundException {
        //given
        final HttpHeaders headers = new HttpHeaders();

        final Map<String, Object> urlParams = new HashMap<>();
        urlParams.put("email", "juju@gmail.com");

        //create and save a tour
        TourEntity tour = TourEntity.builder().reference("T123G-B").letter("G").distanceToCover(12.1).deliveries(Set.of()).build();
        EmployeeEntity man1 = EmployeeEntity.builder().email("antoinedupont@gmail.com").trigram("ant").photo(".png").lastName("okj").firstName("jd").mobilePhone("098Y5E").build();
        EmployeeEntity man2 = EmployeeEntity.builder().email("juju@gmail.com").trigram("jug").photo(".png").lastName("our").firstName("jug").mobilePhone("098YGED").build();
        WarehouseEntity warehouse =WarehouseEntity.builder().name("Grenis").letter("G").build();
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
        OrderEntity order1=OrderEntity.builder().reference("c23").customer(customer).build();
        customerRepository.save(customer);
        orderRepository.save(order1);

        DeliveryEntity d1=DeliveryEntity.builder().reference("L1").orders(Set.of(order1)).build();
        deliveryRepository.save(d1);
        tour.setDeliveries(Set.of(d1));
        tourRepository.save(tour);

        //create and save a planned day including the above tour
        LocalDate today = LocalDate.now();
        DayEntity day = DayEntity.builder().reference("J123G").date(today).build();
        Set<TourEntity> tours = new HashSet<>();
        tours.add(tour);
        day.setTours(tours);
        dayRepository.save(day);


        //when
        TourDMResponseDTO expectedResponse = tourService.getDeliveryTourOfTheDay(man2.getEmail());

        ResponseEntity<TourDMResponseDTO> response = testRestTemplate.exchange("/api/v2.0/deliveryman/tour?email={email}", HttpMethod.GET, new HttpEntity<>(null, headers), TourDMResponseDTO.class, urlParams);

        //then
        //assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponse);
        verify(tourComponent, times(1)).getTourOfTheDay(anyString());
        verify(tourService, times(1)).getDeliveryTourOfTheDay(anyString());

    }
}
