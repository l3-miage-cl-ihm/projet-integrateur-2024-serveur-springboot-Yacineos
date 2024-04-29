package fr.uga.l3miage.integrator.controllers;

import fr.uga.l3miage.integrator.components.DayComponent;
import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.exceptions.NotFoundErrorResponse;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import fr.uga.l3miage.integrator.services.DayService;
import fr.uga.l3miage.integrator.services.TourService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,properties = {"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect","spring.jpa.properties.hibernate.enable_lazy_load_no_trans=true"})
@AutoConfigureTestDatabase
@AutoConfigureWebClient
public class PlannerControllersTest {
    @Autowired
    private TestRestTemplate testRestTemplate;
    @SpyBean
    private DayService dayService;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private DayRepository dayRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TruckRepository truckRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;
    @Autowired
    private OrderRepository orderRepository;
    @SpyBean
    private DayComponent dayComponent;

    @AfterEach
    void clear(){
        dayRepository.deleteAll();
        tourRepository.deleteAll();
        deliveryRepository.deleteAll();
        orderRepository.deleteAll();
        truckRepository.deleteAll();
        employeeRepository.deleteAll();
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

        ResponseEntity<DayResponseDTO> response = testRestTemplate.exchange("/api/v2.0/day?date={date}", HttpMethod.GET, new HttpEntity<>(null, headers), DayResponseDTO.class, urlParams);

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
        urlParams.put("date", LocalDate.of(2024,4,29).toString());

        NotFoundErrorResponse expectedResponse = NotFoundErrorResponse.builder().uri("/api/v2.0/day").errorMessage("No day found for the "+LocalDate.of(2024,4,29)).build();

        ResponseEntity<NotFoundErrorResponse> response = testRestTemplate.exchange("/api/v2.0/day?date={date}", HttpMethod.GET, new HttpEntity<>(null, headers), NotFoundErrorResponse.class, urlParams);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).usingRecursiveComparison().isEqualTo(expectedResponse);
        verify(dayComponent, times(1)).getDay(any());
        verify(dayService, times(1)).getDay(any());
    }
}
