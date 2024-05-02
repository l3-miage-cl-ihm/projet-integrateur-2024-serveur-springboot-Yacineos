package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.DayComponent;
import fr.uga.l3miage.integrator.components.DeliveryComponent;
import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.CustomerState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.exceptions.rest.DayCreationRestException;
import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.mappers.DayPlannerMapper;
import fr.uga.l3miage.integrator.mappers.DeliveryDMMapper;
import fr.uga.l3miage.integrator.mappers.DeliveryPlannerMapper;
import fr.uga.l3miage.integrator.mappers.TourPlannerMapper;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.EmployeeRepository;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import fr.uga.l3miage.integrator.repositories.TruckRepository;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)

public class DayServiceTest {
    @Autowired
    private DayService dayService;

    @MockBean
    private DayComponent dayComponent;
    @MockBean
    private OrderRepository orderRepository;
    @MockBean
    private TruckRepository truckRepository;
    @MockBean
    private EmployeeRepository employeeRepository;
    @SpyBean
    private DayPlannerMapper dayPlannerMapper;
    @SpyBean
    private TourPlannerMapper tourPlannerMapper;
    @SpyBean
    private DeliveryPlannerMapper deliveryPlannerMapper;

    @Test
    void planDayOK() throws  InvalidInputValueException {

        //given
        Set<TourCreationRequest> tourCreationRequestSet= new HashSet<>();
        TruckEntity truck= TruckEntity.builder().immatriculation("AY-124-GF").build();

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
                .distanceToCover(23)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(),deliveryman2.getTrigram()))
                .deliveries(deliveryCreationRequestSet)
                .build();

        tourCreationRequestSet.add(t1);
        DayCreationRequest dayCreationRequest= DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(tourCreationRequestSet)
                .build();


         dayComponent.planDay(dayPlannerMapper.toEntity(dayCreationRequest));
         dayService.planDay(dayCreationRequest);


        //when
        when(dayComponent.isDayAlreadyPlanned(any(LocalDate.class))).thenReturn(false);
        when(truckRepository.findById(truck.getImmatriculation())).thenReturn(Optional.of(truck));
        when(orderRepository.findById(o1.getReference())).thenReturn(Optional.of(o1));
        when(orderRepository.findById(o2.getReference())).thenReturn(Optional.of(o2));
        when(orderRepository.findById(o3.getReference())).thenReturn(Optional.of(o3));
        when(employeeRepository.findById(deliveryman1.getTrigram())).thenReturn(Optional.of(deliveryman1));
        when(employeeRepository.findById(deliveryman2.getTrigram())).thenReturn(Optional.of(deliveryman2));

        //then
        verify(dayComponent,times(1)).planDay(any(DayEntity.class));
        verify(dayPlannerMapper,times(1)).toEntity(dayCreationRequest);
        verify(tourPlannerMapper,times(1)).toEntity(any(TourCreationRequest.class),anyString());
        verify(deliveryPlannerMapper,times(2)).toEntity(any(DeliveryCreationRequest.class),anyString());
        verify(employeeRepository,times(2)).findById(anyString());
        verify(orderRepository,times(3)).findById(anyString());






    }
}
