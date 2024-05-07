package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.*;
import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.CustomerState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.exceptions.rest.DayCreationRestException;
import fr.uga.l3miage.integrator.exceptions.rest.EntityNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.mappers.DayPlannerMapper;
import fr.uga.l3miage.integrator.mappers.DeliveryPlannerMapper;
import fr.uga.l3miage.integrator.mappers.TourPlannerMapper;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import fr.uga.l3miage.integrator.responses.datatypes.MultipleOrder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    private WarehouseRepository warehouseRepository;
    @MockBean
    private DeliveryComponent deliveryComponent;
    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private TourComponent tourComponent;
    @MockBean
    private EmployeeComponent employeeComponent;
    @MockBean
    private OrderComponent orderComponent;
    @MockBean
    private TruckComponent truckComponent;
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
        //IMPORTANT: do not forget the planner because as decided before, he is one to manage the warehouse and it is loaded in the DayPlannerMapper
        WarehouseEntity grenis = WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards", "65001", "San antonio")).trucks(Set.of()).build();
        warehouseRepository.save(grenis);
        EmployeeEntity planner = EmployeeEntity.builder()
                .trigram("STR")
                .email("claudiatessiere@grenis.com").job(Job.PLANNER).photo("claudia.png")
                .lastName("TESSIERE").firstName("claudia").mobilePhone("07654377876").warehouse(grenis).build();
        employeeRepository.save(planner);

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
        when(employeeRepository.findById(planner.getTrigram())).thenReturn(Optional.of(planner));
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
        verify(employeeRepository,times(3)).findById(anyString());
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
        //IMPORTANT: do not forget the planner because as decided before, he is one to manage the warehouse and it is loaded in the DayPlannerMapper
        WarehouseEntity grenis = WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards", "65001", "San antonio")).trucks(Set.of()).build();
        warehouseRepository.save(grenis);
        EmployeeEntity planner = EmployeeEntity.builder().email("claudiatessiere@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("TESSIERE").firstName("claudia").mobilePhone("0765437876").trigram("STR").warehouse(grenis).build();
        employeeRepository.save(planner);

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
                .orders(Set.of(o1.getReference(),"c1023"))  //the order "c123" doesn't exist so it should throw an exception
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
        when(employeeRepository.findById(planner.getTrigram())).thenReturn(Optional.of(planner));
        when(orderRepository.findById(o1.getReference())).thenReturn(Optional.of(o1));
        when(orderRepository.findById("c1023")).thenReturn(Optional.empty());  //OrderEntity not found
        when(orderRepository.findById(o3.getReference())).thenReturn(Optional.of(o3));
        Mockito.doNothing().when(deliveryComponent).saveDelivery(any(DeliveryEntity.class));
        Mockito.doNothing().when(tourComponent).saveTour(any(TourEntity.class));


        //then
        assertThrows(DayCreationRestException.class,()-> dayService.planDay(dayCreationRequest));
        verify(dayComponent,times(0)).planDay(any(DayEntity.class));
        verify(dayPlannerMapper,times(1)).toEntity(any(DayCreationRequest.class));
        verify(tourPlannerMapper,times(1)).toEntity(any(TourCreationRequest.class),anyString());
        verify(deliveryPlannerMapper,times(1)).toEntity(any(DeliveryCreationRequest.class),anyString());
        verify(employeeRepository,times(3)).findById(anyString());



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




    }


    @Test
    void getDayNotFound() throws  DayNotFoundException {
        //when
        when(dayComponent.getDay(any())).thenThrow(new DayNotFoundException("No day was found !"));

        //then
        assertThrows( EntityNotFoundRestException.class,()-> dayService.getDay(LocalDate.now()));
    }


    @Test
    void getDayOK() throws  DayNotFoundException {

        //given
        // creation deliveryMen 1
        Set<TourEntity> tours= new HashSet<>();
        Set<EmployeeEntity> deliverymen= new HashSet<>();
        EmployeeEntity m1=EmployeeEntity.builder().email("jojo@gmail.com").build();
        EmployeeEntity m2=EmployeeEntity.builder().email("axel@gmail.com").build();
        deliverymen.add(m1);
        deliverymen.add(m2);
        //Creation delivery 1
        //creation order
        OrderEntity order11=OrderEntity.builder().reference("c11").build();
        OrderEntity order12=OrderEntity.builder().reference("c12").build();
        Set<OrderEntity> orders1 = new HashSet<>();
        orders1.add(order11);
        orders1.add(order12);
        DeliveryEntity del1=DeliveryEntity.builder().reference("T238G-A1").build();
        del1.setOrders(orders1);
        //Creation delivery 2
        //creation order
        OrderEntity order21=OrderEntity.builder().reference("c21").build();
        OrderEntity order22=OrderEntity.builder().reference("c22").build();
        Set<OrderEntity> orders2 = new HashSet<>();
        orders2.add(order11);
        orders2.add(order12);
        DeliveryEntity del2=DeliveryEntity.builder().reference("T238G-A2").build();
        del2.setOrders(orders2);
        Set<DeliveryEntity> deliveries1=new HashSet<>();
        deliveries1.add(del1);
        deliveries1.add(del2);
        //creation tour 1
        TruckEntity truck1=TruckEntity.builder().immatriculation("EV-666-IL").build();
        TourEntity tour1= TourEntity.builder().reference("T238G-A").build();
        tour1.setDeliverymen(deliverymen);
        tour1.setTruck(truck1);
        tour1.setDeliveries(deliveries1);
        tours.add(tour1);
        // creation deliveryMen 2
        Set<EmployeeEntity> deliverymen2= new HashSet<>();
        EmployeeEntity m3=EmployeeEntity.builder().email("juju@gmail.com").build();
        EmployeeEntity m4=EmployeeEntity.builder().email("alexis@gmail.com").build();
        deliverymen2.add(m3);
        deliverymen2.add(m4);
        //Creation delivery 3
        //creation order
        OrderEntity order31=OrderEntity.builder().reference("c31").build();
        OrderEntity order32=OrderEntity.builder().reference("c32").build();
        Set<OrderEntity> orders3 = new HashSet<>();
        orders3.add(order31);
        orders3.add(order32);
        DeliveryEntity del3=DeliveryEntity.builder().reference("T238G-B1").build();
        del3.setOrders(orders3);
        //Creation delivery 4
        //creation order
        OrderEntity order41=OrderEntity.builder().reference("c41").build();
        OrderEntity order42=OrderEntity.builder().reference("c42").build();
        Set<OrderEntity> orders4 = new HashSet<>();
        orders4.add(order11);
        orders4.add(order12);
        DeliveryEntity del4=DeliveryEntity.builder().reference("T238G-B2").build();
        del4.setOrders(orders4);
        Set<DeliveryEntity> deliveries2=new HashSet<>();
        deliveries2.add(del3);
        deliveries2.add(del4);
        //creation tour 2
        TruckEntity truck2=TruckEntity.builder().immatriculation("AB-345-CD").build();
        TourEntity tour2= TourEntity.builder().reference("T238G-B").build();
        tour2.setDeliverymen(deliverymen2);
        tour2.setTruck(truck2);
        tour2.setDeliveries(deliveries2);
        tours.add(tour2);

        // creation day
        DayEntity day = DayEntity.builder().reference("J238").date(LocalDate.of(2024,4,29)).build();
        day.setTours(tours);

        //when
        when(dayComponent.getDay(any())).thenReturn(day);

        DayResponseDTO expectedResponse= dayPlannerMapper.toResponse(day);
        DayResponseDTO response= dayService.getDay(LocalDate.of(2024,4,29));

        //then
        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);

        verify(dayPlannerMapper,times(2)).toResponse(day);
        verify(tourPlannerMapper,times(4)).toResponse(any());
        verify(deliveryPlannerMapper,times(8)).toResponse(any());
        verify(dayComponent,times(1)).getDay(LocalDate.of(2024,4,29));


    }
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

        Set<String> ref = new LinkedHashSet<>();
        ref.add(o1.getReference());
        ref.add(o2.getReference());
        MultipleOrder m1 = new MultipleOrder(ref,a1.toString());
        MultipleOrder m2 = new MultipleOrder(Set.of(o3.getReference()),a2.toString());
        LinkedHashSet<MultipleOrder> m3 = new LinkedHashSet<>();
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
        assertThat(response.getTrucks().size()).isEqualTo(2);
        assertThat(response.getMultipleOrders().stream().findFirst().get().getAddress()).isEqualTo("21 rue de la paix, Grenoble");

    }




}





