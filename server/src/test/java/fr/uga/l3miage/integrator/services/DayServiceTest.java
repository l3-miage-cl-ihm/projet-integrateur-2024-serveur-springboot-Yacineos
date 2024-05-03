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
import fr.uga.l3miage.integrator.mappers.DeliveryPlannerMapper;
import fr.uga.l3miage.integrator.mappers.TourPlannerMapper;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.DeliveryRepository;
import fr.uga.l3miage.integrator.repositories.EmployeeRepository;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import fr.uga.l3miage.integrator.repositories.TruckRepository;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private DeliveryComponent deliveryComponent;
    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private TourComponent tourComponent;
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

        //when

        when(dayComponent.isDayAlreadyPlanned(any(LocalDate.class))).thenReturn(false);
        when(tourComponent.generateTourReference(any(LocalDate.class),any(Integer.class))).thenReturn("t123G-A");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),0)).thenReturn("c001");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),1)).thenReturn("c002");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),2)).thenReturn("c003");
        when(truckRepository.findById(truck.getImmatriculation())).thenReturn(Optional.of(truck));
        when(employeeRepository.findById(deliveryman1.getTrigram())).thenReturn(Optional.of(deliveryman1));
        when(employeeRepository.findById(deliveryman2.getTrigram())).thenReturn(Optional.of(deliveryman2));
        when(orderRepository.findById(o1.getReference())).thenReturn(Optional.of(o1));
        when(orderRepository.findById(o2.getReference())).thenReturn(Optional.of(o2));
        when(orderRepository.findById(o3.getReference())).thenReturn(Optional.of(o3));
        Mockito.doNothing().when(deliveryComponent).saveDelivery(any(DeliveryEntity.class));
        Mockito.doNothing().when(tourComponent).saveTour(any(TourEntity.class));


        dayService.planDay(dayCreationRequest);



        //then
        verify(dayComponent,times(1)).planDay(any(DayEntity.class));
        verify(dayPlannerMapper,times(1)).toEntity(any(DayCreationRequest.class));
        verify(tourPlannerMapper,times(1)).toEntity(any(TourCreationRequest.class),anyString());
        verify(deliveryPlannerMapper,times(2)).toEntity(any(DeliveryCreationRequest.class),anyString());
        verify(employeeRepository,times(2)).findById(anyString());
        verify(orderRepository,times(3)).findById(anyString());






    }

    @Test    //missed deliveryman or (deliverymen.size()!=1 and deliverymen.size()!=2)
    void planDay_NotOK_BecauseOf_MissedInputs() throws InvalidInputValueException {
        //given
        Set<TourCreationRequest> tourCreationRequestSet= new HashSet<>();
        TruckEntity truck= TruckEntity.builder().immatriculation("AY-124-GF").build();


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
                .deliverymen(Set.of())
                .deliveries(deliveryCreationRequestSet)
                .build();

        tourCreationRequestSet.add(t1);
        DayCreationRequest dayCreationRequest= DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(tourCreationRequestSet)
                .build();

        //when

        when(dayComponent.isDayAlreadyPlanned(any(LocalDate.class))).thenReturn(false);
        when(tourComponent.generateTourReference(any(LocalDate.class),any(Integer.class))).thenReturn("t123G-A");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),0)).thenReturn("c001");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),1)).thenReturn("c002");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),2)).thenReturn("c003");
        when(truckRepository.findById(truck.getImmatriculation())).thenReturn(Optional.of(truck));
        when(orderRepository.findById(o1.getReference())).thenReturn(Optional.of(o1));
        when(orderRepository.findById(o2.getReference())).thenReturn(Optional.of(o2));
        when(orderRepository.findById(o3.getReference())).thenReturn(Optional.of(o3));
        Mockito.doNothing().when(deliveryComponent).saveDelivery(any(DeliveryEntity.class));
        Mockito.doNothing().when(tourComponent).saveTour(any(TourEntity.class));






        //then
        assertThrows(DayCreationRestException.class,()->dayService.planDay(dayCreationRequest));
        verify(dayPlannerMapper,times(0)).toEntity(any(DayCreationRequest.class));
        verify(tourPlannerMapper,times(0)).toEntity(any(TourCreationRequest.class),anyString());
        verify(deliveryPlannerMapper,times(0)).toEntity(any(DeliveryCreationRequest.class),anyString());
        verify(employeeRepository,times(0)).findById(anyString());
        verify(orderRepository,times(0)).findById(anyString());


    }



    @Test   //wrong inputs such as wrong order reference or wrong deliveryman trigram
    void planDay_NotOK_BecauseOf_WrongInputs_NotFoundEntities() throws InvalidInputValueException {
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
                .orders(Set.of(o1.getReference(),"c123"))  //the order "c123" doesn't exist so it should throw an exception
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

        //when

        when(dayComponent.isDayAlreadyPlanned(any(LocalDate.class))).thenReturn(false);
        when(tourComponent.generateTourReference(any(LocalDate.class),any(Integer.class))).thenReturn("t123G-A");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),0)).thenReturn("c001");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),1)).thenReturn("c002");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),2)).thenReturn("c003");
        when(truckRepository.findById(truck.getImmatriculation())).thenReturn(Optional.of(truck));
        when(employeeRepository.findById(deliveryman1.getTrigram())).thenReturn(Optional.of(deliveryman1));
        when(employeeRepository.findById(deliveryman2.getTrigram())).thenReturn(Optional.of(deliveryman2));
        when(orderRepository.findById(o1.getReference())).thenReturn(Optional.of(o1));
        when(orderRepository.findById("c123")).thenReturn(Optional.empty());  //OrderEntity not found
        when(orderRepository.findById(o3.getReference())).thenReturn(Optional.of(o3));
        Mockito.doNothing().when(deliveryComponent).saveDelivery(any(DeliveryEntity.class));
        Mockito.doNothing().when(tourComponent).saveTour(any(TourEntity.class));


        //then
        assertThrows(DayCreationRestException.class,()-> dayService.planDay(dayCreationRequest));
        verify(dayComponent,times(0)).planDay(any(DayEntity.class));
        verify(dayPlannerMapper,times(1)).toEntity(any(DayCreationRequest.class));
        verify(tourPlannerMapper,times(1)).toEntity(any(TourCreationRequest.class),anyString());
        verify(deliveryPlannerMapper,times(1)).toEntity(any(DeliveryCreationRequest.class),anyString());
        verify(employeeRepository,times(2)).findById(anyString());



    }


    @Test   //wrong inputs: day already planned for given date .
    void planDay_NotOK_BecauseOf_WrongInputs_DayAlreadyPlanned() throws InvalidInputValueException {
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
                .orders(Set.of(o1.getReference(),"c123"))  //the order "c123" doesn't exist so it should throw an exception
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

        //when

        when(dayComponent.isDayAlreadyPlanned(any(LocalDate.class))).thenReturn(true);
        when(tourComponent.generateTourReference(any(LocalDate.class),any(Integer.class))).thenReturn("t123G-A");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),0)).thenReturn("c001");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),1)).thenReturn("c002");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),2)).thenReturn("c003");
        when(truckRepository.findById(truck.getImmatriculation())).thenReturn(Optional.of(truck));
        when(employeeRepository.findById(deliveryman1.getTrigram())).thenReturn(Optional.of(deliveryman1));
        when(employeeRepository.findById(deliveryman2.getTrigram())).thenReturn(Optional.of(deliveryman2));
        when(orderRepository.findById(o1.getReference())).thenReturn(Optional.of(o1));
        when(orderRepository.findById("c123")).thenReturn(Optional.empty());  //OrderEntity not found
        when(orderRepository.findById(o3.getReference())).thenReturn(Optional.of(o3));
        Mockito.doNothing().when(deliveryComponent).saveDelivery(any(DeliveryEntity.class));
        Mockito.doNothing().when(tourComponent).saveTour(any(TourEntity.class));


        //then
        assertThrows(DayCreationRestException.class,()-> dayService.planDay(dayCreationRequest));
        verify(dayComponent,times(0)).planDay(any(DayEntity.class));
        verify(dayPlannerMapper,times(0)).toEntity(any(DayCreationRequest.class));
        verify(tourPlannerMapper,times(0)).toEntity(any(TourCreationRequest.class),anyString());
        verify(deliveryPlannerMapper,times(0)).toEntity(any(DeliveryCreationRequest.class),anyString());
        verify(employeeRepository,times(0)).findById(anyString());
        verify(orderRepository,times(0)).findById(anyString());




    }}





