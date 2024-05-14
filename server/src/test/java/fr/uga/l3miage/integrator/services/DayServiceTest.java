package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.*;
import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.enums.*;
import fr.uga.l3miage.integrator.exceptions.rest.*;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.exceptions.technical.UpdateDayStateException;
import fr.uga.l3miage.integrator.exceptions.technical.WarehouseNotFoundException;
import fr.uga.l3miage.integrator.mappers.DayPlannerMapper;
import fr.uga.l3miage.integrator.mappers.DeliveryPlannerMapper;
import fr.uga.l3miage.integrator.mappers.TourPlannerMapper;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.DeliveryPlannerResponseDTO;
import fr.uga.l3miage.integrator.responses.SetUpBundleResponse;
import fr.uga.l3miage.integrator.responses.TourPlannerResponseDTO;
import fr.uga.l3miage.integrator.responses.datatypes.MultipleOrder;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.*;

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
    private WarehouseComponent warehouseComponent;
    @SpyBean
    private DayPlannerMapper dayPlannerMapper;
    @SpyBean
    private TourPlannerMapper tourPlannerMapper;
    @SpyBean
    private DeliveryPlannerMapper deliveryPlannerMapper;


    @Test
    void planDayOK() throws  InvalidInputValueException {
        //given
        List<TourCreationRequest> tourCreationRequestSet= new ArrayList<>();
        TruckEntity truck= TruckEntity.builder().immatriculation("AY-124-GF").build();
        //IMPORTANT: do not forget the planner because as decided before, he is one to manage the warehouse and it is loaded in the DayPlannerMapper
        WarehouseEntity grenis = WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards", "65001", "San antonio")).trucks(Set.of()).build();
        EmployeeEntity planner = EmployeeEntity.builder()
                .trigram("STR")
                .email("claudiatessiere@grenis.com").job(Job.PLANNER).photo("claudia.png")
                .lastName("TESSIERE").firstName("claudia").mobilePhone("07654377876").warehouse(grenis).build();


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

        List<DeliveryCreationRequest> deliveryCreationRequestList=new ArrayList<>();
        DeliveryCreationRequest d1= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o1.getReference(),o2.getReference()))
                .coordinates(List.of())
                .coordinates(List.of(23.8,23.7))
                .build();

        DeliveryCreationRequest d2= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o3.getReference()))
                .coordinates(List.of())
                .coordinates(List.of(23.8,23.7))
                .build();

        deliveryCreationRequestList.add(d1);
        deliveryCreationRequestList.add(d2);
        TourCreationRequest t1= TourCreationRequest.builder()
                .distanceToCover(23)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(),deliveryman2.getTrigram()))
                .deliveries(deliveryCreationRequestList)
                .build();

        tourCreationRequestSet.add(t1);
        DayCreationRequest dayCreationRequest= DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(tourCreationRequestSet)
                .build();

        //when

        when(dayComponent.isDayAlreadyPlanned(any(LocalDate.class))).thenReturn(false);
        when(tourComponent.generateTourReference(any(LocalDate.class),any(Integer.class))).thenReturn("t123G-A");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),0,"A")).thenReturn("t123G-A1");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),1,"A")).thenReturn("t123G-A2");
        when(deliveryComponent.generateDeliveryReference(dayCreationRequest.getDate(),2,"A")).thenReturn("t123G-A3");
        when(truckRepository.findById(truck.getImmatriculation())).thenReturn(Optional.of(truck));
        when(employeeRepository.findById(deliveryman1.getTrigram())).thenReturn(Optional.of(deliveryman1));
        when(employeeRepository.findById(deliveryman2.getTrigram())).thenReturn(Optional.of(deliveryman2));
        when(employeeRepository.findById(planner.getTrigram())).thenReturn(Optional.of(planner));
        when(warehouseRepository.findById(grenis.getName())).thenReturn(Optional.of(grenis));
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
        TruckEntity truck= TruckEntity.builder().immatriculation("AY-124-GF").build();
        TourCreationRequest t1= TourCreationRequest.builder()
                .distanceToCover(23)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of())
                .deliveries(List.of())
                .build();

        DayCreationRequest dayCreationRequest= DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(List.of(t1))
                .build();

        //when
        when(dayComponent.isDayAlreadyPlanned(any(LocalDate.class))).thenReturn(false);
        when(tourComponent.generateTourReference(any(LocalDate.class),any(Integer.class))).thenReturn("t123G-A");
        //then
        assertThrows(DayCreationRestException.class,()->dayService.planDay(dayCreationRequest));
        verify(dayPlannerMapper,times(0)).toEntity(any(DayCreationRequest.class));
        verify(tourPlannerMapper,times(0)).toEntity(any(TourCreationRequest.class),anyString());


    }

    @Test   //wrong inputs such as wrong order reference or wrong deliveryman trigram
    void planDay_NotOK_BecauseOf_WrongInputs_NotFoundEntities() throws InvalidInputValueException {
        //given
        DayCreationRequest dayCreationRequest= DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(List.of())
                .build();

        //when
        when(dayComponent.isDayAlreadyPlanned(any(LocalDate.class))).thenReturn(false);

        //then
        assertThrows(DayCreationRestException.class,()-> dayService.planDay(dayCreationRequest));
        verify(dayComponent,times(0)).planDay(any(DayEntity.class));


    }


    @Test   //wrong inputs: day already planned for given date .
    void planDay_NotOK_BecauseOf_WrongInputs_DayAlreadyPlanned() throws InvalidInputValueException {
        //given
        DayCreationRequest dayCreationRequest= DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(List.of())
                .build();

        //when
        when(dayComponent.isDayAlreadyPlanned(any(LocalDate.class))).thenReturn(true);

        //then
        assertThrows(DayCreationRestException.class,()-> dayService.planDay(dayCreationRequest));
        verify(dayComponent,times(0)).planDay(any(DayEntity.class));

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
        List<TourEntity> tours= new ArrayList<>();
        Set<EmployeeEntity> deliverymen= new HashSet<>();
        WarehouseEntity warehouse=WarehouseEntity.builder().name("Grenis").coordinates(new Coordinates(23.8,15.8765)).build();
        EmployeeEntity m1=EmployeeEntity.builder().email("jojo@gmail.com").firstName("Jug").lastName("OURZIK").warehouse(warehouse).trigram("JOK").build();
        EmployeeEntity m2=EmployeeEntity.builder().email("axel@gmail.com").firstName("Juju").lastName("LEROY").warehouse(warehouse).trigram("JLY").build();
        deliverymen.add(m1);
        deliverymen.add(m2);
        //Creation delivery 1
        //creation order
        CustomerEntity customer1=CustomerEntity.builder().address(new Address("2 rue de paris","76000","Paris")).firstName("Rony").lastName("Diiiirect").build();
        CustomerEntity customer2=CustomerEntity.builder().address(new Address("16 rue de caen","34650","Caen")).firstName("Jug").lastName("Diiiirectman").build();
        CustomerEntity customer3=CustomerEntity.builder().address(new Address("02 rue de toulon","10002","Toulon")).firstName("mely").lastName("joie").build();
        CustomerEntity customer4=CustomerEntity.builder().address(new Address("02 rue de marseille","13003","Marseille")).firstName("melyssa").lastName("bondy").build();

        OrderEntity order11=OrderEntity.builder().reference("c11").customer(customer1).build();
        OrderEntity order12=OrderEntity.builder().reference("c12").customer(customer1).build();
        Set<OrderEntity> orders1 = new HashSet<>();
        orders1.add(order11);
        orders1.add(order12);
        DeliveryEntity del1=DeliveryEntity.builder().reference("l238G-A1").coordinates(new Coordinates(12.87,15.876)).build();
        del1.setOrders(orders1);
        //Creation delivery 2
        //creation order
        OrderEntity order21=OrderEntity.builder().reference("c21").customer(customer2).build();
        OrderEntity order22=OrderEntity.builder().reference("c22").customer(customer2).build();
        Set<OrderEntity> orders2 = new HashSet<>();
        orders2.add(order11);
        orders2.add(order12);
        DeliveryEntity del2=DeliveryEntity.builder().reference("l238G-A2").coordinates(new Coordinates(12.87,15.876)).build();
        del2.setOrders(orders2);
        List<DeliveryEntity> deliveries1=new ArrayList<>();
        deliveries1.add(del1);
        deliveries1.add(del2);
        //creation tour 1
        TruckEntity truck1=TruckEntity.builder().immatriculation("EV-666-IL").build();
        TourEntity tour1= TourEntity.builder().reference("t238G-A").build();
        tour1.setDeliverymen(deliverymen);
        tour1.setTruck(truck1);
        tour1.setDeliveries(deliveries1);
        tours.add(tour1);
        // creation deliveryMen 2
        Set<EmployeeEntity> deliverymen2= new HashSet<>();
        EmployeeEntity m3=EmployeeEntity.builder().email("juju@gmail.com").firstName("Massi").lastName("GHER").warehouse(warehouse).trigram("MGR").build();
        EmployeeEntity m4=EmployeeEntity.builder().email("alexis@gmail.com").firstName("Samy").lastName("SPINCER").warehouse(warehouse).trigram("SSR").build();
        deliverymen2.add(m3);
        deliverymen2.add(m4);
        //Creation delivery 3
        //creation order
        OrderEntity order31=OrderEntity.builder().reference("c31").customer(customer3).build();
        OrderEntity order32=OrderEntity.builder().reference("c32").customer(customer3).build();
        Set<OrderEntity> orders3 = new HashSet<>();
        orders3.add(order31);
        orders3.add(order32);
        DeliveryEntity del3=DeliveryEntity.builder().reference("l238G-B1").coordinates(new Coordinates(12.87,15.876)).build();
        del3.setOrders(orders3);
        //Creation delivery 4
        //creation order
        OrderEntity order41=OrderEntity.builder().reference("c41").customer(customer4).build();
        OrderEntity order42=OrderEntity.builder().reference("c42").customer(customer4).build();
        Set<OrderEntity> orders4 = new HashSet<>();
        orders4.add(order11);
        orders4.add(order12);
        DeliveryEntity del4=DeliveryEntity.builder().reference("l238G-B2").coordinates(new Coordinates(12.87,15.876)).build();
        del4.setOrders(orders4);

        List<DeliveryEntity> deliveries2=new ArrayList<>();
        deliveries2.add(del3);
        deliveries2.add(del4);
        //creation tour 2
        TruckEntity truck2=TruckEntity.builder().immatriculation("AB-345-CD").build();
        TourEntity tour2= TourEntity.builder().reference("t238G-B").build();
        tour2.setDeliverymen(deliverymen2);
        tour2.setTruck(truck2);
        tour2.setDeliveries(deliveries2);
        tours.add(tour2);

        /******** Expected response creation ******/
        List<DeliveryPlannerResponseDTO> d1= new ArrayList<>();
        List<DeliveryPlannerResponseDTO> d2= new ArrayList<>();

        DeliveryPlannerResponseDTO dp1=new DeliveryPlannerResponseDTO();
        dp1.setAddress("2 rue de paris,Paris");
        dp1.setOrders(Set.of(order11.getReference(),order12.getReference()));
        dp1.setDistanceToCover(0.0);
        dp1.setCoordinates(List.of(23.98,76.87));


        DeliveryPlannerResponseDTO dp2=new DeliveryPlannerResponseDTO();
        dp2.setAddress("16 rue de caen,Caen");
        dp2.setOrders(Set.of(order21.getReference(),order22.getReference()));
        dp2.setDistanceToCover(0.0);
        dp2.setCoordinates(List.of(23.98,76.87));

        DeliveryPlannerResponseDTO dp3=new DeliveryPlannerResponseDTO();
        dp3.setAddress("02 rue de toulon,Toulon");
        dp3.setOrders(Set.of(order31.getReference(),order32.getReference()));
        dp3.setDistanceToCover(0.0);
        dp3.setCoordinates(List.of(23.98,76.87));

        DeliveryPlannerResponseDTO dp4=new DeliveryPlannerResponseDTO();
        dp4.setAddress("02 rue de marseille,Marseille");
        dp4.setOrders(Set.of(order41.getReference(),order42.getReference()));
        dp4.setDistanceToCover(0.0);
        dp4.setCoordinates(List.of(23.98,76.87));
        d1.add(dp1);
        d2.add(dp2);

        TourPlannerResponseDTO t1= new TourPlannerResponseDTO();
        t1.setRefTour(tour1.getReference());
        t1.setDeliverymen(Set.of(m1.getTrigram(),m2.getTrigram()));
        t1.setDeliveries(d1);

        TourPlannerResponseDTO t2= new TourPlannerResponseDTO();
        t2.setRefTour(tour2.getReference());
        t2.setDeliverymen(Set.of(m3.getTrigram(),m4.getTrigram()));
        t2.setDeliveries(d2);

        // creation day
        DayEntity day = DayEntity.builder().reference("J238").date(LocalDate.of(2024,4,29)).build();
        day.setTours(tours);

        //when
        when(dayComponent.getDay(any())).thenReturn(day);

        DayResponseDTO expectedResponse= dayPlannerMapper.toResponse(day);
        List<TourPlannerResponseDTO> toursF =new ArrayList<>();
        toursF.add(t1);
        toursF.add(t2);
        expectedResponse.setTours(toursF);
        DayResponseDTO response= dayService.getDay(LocalDate.of(2024,4,29));

        //then
        assertThat(response.getDate()).isEqualTo(expectedResponse.getDate());
        verify(dayPlannerMapper,times(2)).toResponse(day);
        verify(tourPlannerMapper,times(2)).toResponse(any());
        verify(deliveryPlannerMapper,times(4)).toResponse(any());
        verify(dayComponent,times(1)).getDay(LocalDate.of(2024,4,29));


    }
    @Test
    void getSetUpBundle(){

        Address a1 = new Address("21 rue de la paix","38000","Grenoble");
        Address a2 = new Address("21 rue de la joie","38000","Grenoble");
        WarehouseEntity warehouse=WarehouseEntity.builder().name("Grenis").coordinates(new Coordinates(12.76,14.874)).build();
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

        TruckEntity t1 = TruckEntity.builder()
                .immatriculation("ABC")
                .build();
        TruckEntity t2 = TruckEntity.builder()
                .immatriculation("DEF")
                .build();

        warehouse.setTrucks(Set.of(t1,t2));
        Set<String> ref = new LinkedHashSet<>();
        ref.add(o1.getReference());
        ref.add(o2.getReference());
        MultipleOrder m1 = new MultipleOrder(ref,a1.toString());
        MultipleOrder m2 = new MultipleOrder(Set.of(o3.getReference()),a2.toString());
        LinkedHashSet<MultipleOrder> m3 = new LinkedHashSet<>();
        m3.add(m1);
        m3.add(m2);

        Set<String> employeeIds = new HashSet<>();
        employeeIds.add(e1.getTrigram());
        employeeIds.add(e2.getTrigram());
        employeeIds.add(e3.getTrigram());

        when(employeeComponent.getAllDeliveryMenID((warehouse.getName()))).thenReturn(employeeIds);
        when(warehouseComponent.getAllTrucks(warehouse.getName())).thenReturn(Set.of(t1.getImmatriculation(),t2.getImmatriculation()));
        when(warehouseComponent.getWarehouseCoordinates(warehouse.getName())).thenReturn(warehouse.getCoordinates());
        when(orderComponent.createMultipleOrders()).thenReturn(m3);
        SetUpBundleResponse response = dayService.getSetUpBundle(warehouse.getName());

        assertThat(response.getDeliverymen().size()).isEqualTo(3);
        assertThat(response.getMultipleOrders().size()).isEqualTo(2);
        assertThat(response.getTrucks().size()).isEqualTo(2);
    }

    @Test
    void editDayOK() throws InvalidInputValueException, DayNotFoundException {

        //given
        List<TourCreationRequest> tourCreationRequestSet= new ArrayList<>();
        TruckEntity truck= TruckEntity.builder().immatriculation("AY-124-GF").build();
        //IMPORTANT: do not forget the planner because as decided before, he is one to manage the warehouse and it is loaded in the DayPlannerMapper
        WarehouseEntity grenis = WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards", "65001", "San antonio")).trucks(Set.of()).build();
        EmployeeEntity planner = EmployeeEntity.builder()
                .trigram("STR")
                .email("claudiatessiere@grenis.com").job(Job.PLANNER).photo("claudia.png")
                .lastName("TESSIERE").firstName("claudia").mobilePhone("07654377876").warehouse(grenis).build();


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

        OrderEntity o4= OrderEntity.builder()
                .reference("c004")
                .creationDate(LocalDate.now())
                .state(OrderState.OPENED)
                .lines(Set.of())
                .customer(customer2)
                .build();

        List<DeliveryCreationRequest> deliveryCreationRequestList=new ArrayList<>();
        DeliveryCreationRequest d1= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o1.getReference(),o2.getReference()))
                .coordinates(List.of(23.8,23.7))
                .build();

        DeliveryCreationRequest d2= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o3.getReference()))
                .coordinates(List.of(23.8,23.7))
                .build();

        deliveryCreationRequestList.add(d1);
        deliveryCreationRequestList.add(d2);
        TourCreationRequest t1= TourCreationRequest.builder()
                .distanceToCover(23)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(),deliveryman2.getTrigram()))
                .deliveries(deliveryCreationRequestList)
                .build();

        tourCreationRequestSet.add(t1);
        LocalDate date=LocalDate.of(2024,05,10);
        DayCreationRequest editDayRequest= DayCreationRequest.builder()
                .date(date)
                .tours(tourCreationRequestSet)
                .build();

        //*************
        DeliveryEntity delivery1=DeliveryEntity.builder()
                .reference("J131G-A1")
                .orders(Set.of(o1,o2))
                .state(DeliveryState.PLANNED)
                .coordinates(new Coordinates(12.87,15.876))
                .build();
        DeliveryEntity delivery2=DeliveryEntity.builder()
                .reference("J131G-A2")
                .orders(Set.of(o3))
                .state(DeliveryState.PLANNED)
                .coordinates(new Coordinates(12.87,15.876))
                .build();
        DeliveryEntity delivery3=DeliveryEntity.builder()
                .reference("J131G-A3")
                .orders(Set.of(o4))
                .state(DeliveryState.PLANNED)
                .coordinates(new Coordinates(12.87,15.876))
                .build();

        TourEntity tour1=TourEntity.builder()
                .reference("J131G-A")
                .state(TourState.PLANNED)
                .truck(truck)
                .deliveries(List.of(delivery1,delivery2))
                .build();

        TourEntity tour2=TourEntity.builder()
                .reference("J131G-B")
                .state(TourState.PLANNED)
                .truck(truck)
                .deliveries(List.of(delivery3))
                .build();
        List<TourEntity> tours=new ArrayList<>();
        tours.add(tour1);
        tours.add(tour2);
        DayEntity day=DayEntity.builder().date(date).reference("J131G")
                .planner(planner)
                .state(DayState.PLANNED)
                .tours(tours)
                .build();

        //*************
        //when
        when(dayComponent.getDayById(anyString())).thenReturn(day);
        when(tourComponent.generateTourReference(any(LocalDate.class),any(Integer.class))).thenReturn("t131G-A");
        when(deliveryComponent.generateDeliveryReference(editDayRequest.getDate(),1,"A")).thenReturn("t131G-A1");
        when(deliveryComponent.generateDeliveryReference(editDayRequest.getDate(),2,"A")).thenReturn("t131G-A2");

        when(truckRepository.findById(truck.getImmatriculation())).thenReturn(Optional.of(truck));
        when(employeeRepository.findById(deliveryman1.getTrigram())).thenReturn(Optional.of(deliveryman1));
        when(employeeRepository.findById(deliveryman2.getTrigram())).thenReturn(Optional.of(deliveryman2));
        when(employeeRepository.findById(planner.getTrigram())).thenReturn(Optional.of(planner));
        when(warehouseRepository.findById(grenis.getName())).thenReturn(Optional.of(grenis));

        when(orderRepository.findById(o1.getReference())).thenReturn(Optional.of(o1));
        when(orderRepository.findById(o2.getReference())).thenReturn(Optional.of(o2));
        when(orderRepository.findById(o3.getReference())).thenReturn(Optional.of(o3));
        Mockito.doNothing().when(deliveryComponent).saveDelivery(any(DeliveryEntity.class));
        Mockito.doNothing().when(tourComponent).saveTour(any(TourEntity.class));

        dayService.editDay(editDayRequest,day.getReference());

        //then
        verify(dayComponent,times(1)).planDay(any(DayEntity.class));
        verify(dayPlannerMapper,times(1)).toEntity(any(DayCreationRequest.class));
        verify(tourPlannerMapper,times(1)).toEntity(any(TourCreationRequest.class),anyString());
        verify(deliveryPlannerMapper,times(2)).toEntity(any(DeliveryCreationRequest.class),anyString());
        verify(employeeRepository,times(3)).findById(anyString());
        verify(orderRepository,times(3)).findById(anyString());

    }
    @Test    //missed deliveryman or (deliverymen.size()!=1 and deliverymen.size()!=2)
    void editDay_NotOK_BecauseOf_MissedInputs() throws InvalidInputValueException, DayNotFoundException {
        //given
        List<TourCreationRequest> tourCreationRequestList= new ArrayList<>();
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

        List<DeliveryCreationRequest> deliveryCreationRequestList=new ArrayList<>();
        DeliveryCreationRequest d1= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o1.getReference(),o2.getReference()))
                .build();

        DeliveryCreationRequest d2= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o3.getReference()))
                .build();

        deliveryCreationRequestList.add(d1);
        deliveryCreationRequestList.add(d2);
        TourCreationRequest t1= TourCreationRequest.builder()
                .distanceToCover(23)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of())
                .deliveries(deliveryCreationRequestList)
                .build();

        tourCreationRequestList.add(t1);
        DayCreationRequest editDayRequest= DayCreationRequest.builder()
                .date(LocalDate.now())
                .tours(tourCreationRequestList)
                .build();

        DeliveryEntity delivery1=DeliveryEntity.builder()
                .reference("J131G-A1")
                .orders(Set.of(o1,o2))
                .state(DeliveryState.PLANNED)
                .build();
        DeliveryEntity delivery2=DeliveryEntity.builder()
                .reference("J131G-A2")
                .orders(Set.of(o3))
                .state(DeliveryState.PLANNED)
                .build();

        TourEntity tour1=TourEntity.builder()
                .reference("J131G-A")
                .state(TourState.PLANNED)
                .truck(truck)
                .deliveries(List.of(delivery1,delivery2))
                .build();

        List<TourEntity> tours=new ArrayList<>();
        tours.add(tour1);

        LocalDate date=LocalDate.of(2024,05,10);
        DayEntity day=DayEntity.builder().date(date).reference("J131G")
                .state(DayState.PLANNED)
                .tours(tours)
                .build();

        //*************
        //when
        when(dayComponent.getDayById(day.getReference())).thenReturn(day);
        when(tourComponent.generateTourReference(any(LocalDate.class),any(Integer.class))).thenReturn("t131G-A");
        when(deliveryComponent.generateDeliveryReference(editDayRequest.getDate(),1,"A")).thenReturn("t131G-A1");
        when(deliveryComponent.generateDeliveryReference(editDayRequest.getDate(),2,"A")).thenReturn("t131G-A2");
        when(truckRepository.findById(truck.getImmatriculation())).thenReturn(Optional.of(truck));
        when(orderRepository.findById(o1.getReference())).thenReturn(Optional.of(o1));
        when(orderRepository.findById(o2.getReference())).thenReturn(Optional.of(o2));
        when(orderRepository.findById(o3.getReference())).thenReturn(Optional.of(o3));
        Mockito.doNothing().when(deliveryComponent).saveDelivery(any(DeliveryEntity.class));
        Mockito.doNothing().when(tourComponent).saveTour(any(TourEntity.class));

        //then
        assertThrows(DayCreationRestException.class,()->dayService.editDay(editDayRequest,day.getReference()));
        verify(dayPlannerMapper,times(0)).toEntity(any(DayCreationRequest.class));
        verify(tourPlannerMapper,times(0)).toEntity(any(TourCreationRequest.class),anyString());
        verify(deliveryPlannerMapper,times(0)).toEntity(any(DeliveryCreationRequest.class),anyString());
        verify(employeeRepository,times(0)).findById(anyString());
        verify(orderRepository,times(0)).findById(anyString());

    }

    @Test    //missed deliveryman or (deliverymen.size()!=1 and deliverymen.size()!=2)
    void editDay_NotOK_BecauseOf_DifferentProvidedDate() throws InvalidInputValueException, DayNotFoundException {
        //given
        List<TourCreationRequest> tourCreationRequestList= new ArrayList<>();
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

        List<DeliveryCreationRequest> deliveryCreationRequestList=new ArrayList<>();
        DeliveryCreationRequest d1= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o1.getReference(),o2.getReference()))
                .build();

        DeliveryCreationRequest d2= DeliveryCreationRequest.builder()
                .distanceToCover(12)
                .orders(Set.of(o3.getReference()))
                .build();

        deliveryCreationRequestList.add(d1);
        deliveryCreationRequestList.add(d2);
        TourCreationRequest t1= TourCreationRequest.builder()
                .distanceToCover(23)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of())
                .deliveries(deliveryCreationRequestList)
                .build();

        tourCreationRequestList.add(t1);
        DayCreationRequest editDayRequest= DayCreationRequest.builder()
                .date(LocalDate.of(2024,05,11))
                .tours(tourCreationRequestList)
                .build();

        DeliveryEntity delivery1=DeliveryEntity.builder()
                .reference("J131G-A1")
                .orders(Set.of(o1,o2))
                .state(DeliveryState.PLANNED)
                .build();
        DeliveryEntity delivery2=DeliveryEntity.builder()
                .reference("J131G-A2")
                .orders(Set.of(o3))
                .state(DeliveryState.PLANNED)
                .build();

        TourEntity tour1=TourEntity.builder()
                .reference("J131G-A")
                .state(TourState.PLANNED)
                .truck(truck)
                .deliveries(List.of(delivery1,delivery2))
                .build();

        List<TourEntity> tours=new ArrayList<>();
        tours.add(tour1);

        LocalDate date=LocalDate.of(2024,05,10);
        DayEntity day=DayEntity.builder().date(date).reference("J131G")
                .state(DayState.PLANNED)
                .tours(tours)
                .build();

        //*************
        //when
        when(dayComponent.getDayById(day.getReference())).thenReturn(day);
        when(tourComponent.generateTourReference(any(LocalDate.class),any(Integer.class))).thenReturn("t131G-A");
        when(deliveryComponent.generateDeliveryReference(editDayRequest.getDate(),1,"A")).thenReturn("t131G-A1");
        when(deliveryComponent.generateDeliveryReference(editDayRequest.getDate(),2,"A")).thenReturn("t131G-A2");
        when(truckRepository.findById(truck.getImmatriculation())).thenReturn(Optional.of(truck));
        when(orderRepository.findById(o1.getReference())).thenReturn(Optional.of(o1));
        when(orderRepository.findById(o2.getReference())).thenReturn(Optional.of(o2));
        when(orderRepository.findById(o3.getReference())).thenReturn(Optional.of(o3));
        Mockito.doNothing().when(deliveryComponent).saveDelivery(any(DeliveryEntity.class));
        Mockito.doNothing().when(tourComponent).saveTour(any(TourEntity.class));

        //then
        assertThrows(DayCreationRestException.class,()->dayService.editDay(editDayRequest,day.getReference()));
        verify(dayPlannerMapper,times(0)).toEntity(any(DayCreationRequest.class));
        verify(tourPlannerMapper,times(0)).toEntity(any(TourCreationRequest.class),anyString());
        verify(deliveryPlannerMapper,times(0)).toEntity(any(DeliveryCreationRequest.class),anyString());
        verify(employeeRepository,times(0)).findById(anyString());
        verify(orderRepository,times(0)).findById(anyString());

    }

    @Test    //missed deliveryman or (deliverymen.size()!=1 and deliverymen.size()!=2)
    void editDay_NotOK_BecauseOf_NotFoundDay() throws InvalidInputValueException, DayNotFoundException {

        DayCreationRequest editDayRequest= DayCreationRequest.builder()
                .date(LocalDate.now())
                .build();
        //when
        when(dayComponent.getDayById(anyString())).thenThrow(new DayNotFoundException("No day was found !"));
        //then
        assertThrows(DayNotFoundRestException.class,()->dayService.editDay(editDayRequest,"J131G"));


    }

    @Test
    void updateDayStateOK1() throws DayNotFoundException, UpdateDayStateException {
        //given
        DayEntity day= DayEntity.builder()
                .state(DayState.PLANNED)
                .tours(List.of())
                .reference("J131G")
                .build();

        //when
        when(dayComponent.getDayById(day.getReference())).thenReturn(day);
        when(dayComponent.updateDayState(day.getReference(),DayState.IN_PROGRESS)).thenReturn(day);
        day.setState(DayState.IN_PROGRESS);
        DayEntity response= dayService.updateDayState(day.getReference(),DayState.IN_PROGRESS);

        //then
       assertThat(response.getState()).isEqualTo(DayState.IN_PROGRESS);

    }

    @Test
    void updateDayStateOK2() throws DayNotFoundException, UpdateDayStateException {
        //given
        TourEntity tour=TourEntity.builder()
                .reference("J131G-A")
                .state(TourState.COMPLETED)
                .build();
        DayEntity day= DayEntity.builder()
                .state(DayState.IN_PROGRESS)
                .tours(List.of())
                .reference("J131G")
                .tours(List.of(tour))
                .build();

        //when
        when(dayComponent.getDayById(day.getReference())).thenReturn(day);
        when(dayComponent.updateDayState(day.getReference(),DayState.COMPLETED)).thenReturn(day);
        day.setState(DayState.COMPLETED);

        DayEntity response= dayService.updateDayState(day.getReference(),DayState.COMPLETED);

        //then
        assertThat(response.getState()).isEqualTo(DayState.COMPLETED);


    }

    @Test
    void updateDayStateNotOK_BecauseOf_NotFoundDay() throws DayNotFoundException, UpdateDayStateException {
        //when
        when(dayComponent.updateDayState(anyString(),any(DayState.class))).thenThrow(new DayNotFoundException("No day was found !"));
        //then
        assertThrows(DayNotFoundRestException.class,()-> dayService.updateDayState("J131G",DayState.IN_PROGRESS));

    }

    @Test
    void updateDayNotOK_BecauseOfWrongState() throws DayNotFoundException, UpdateDayStateException {

        //given
        DayEntity day= DayEntity.builder()
                .state(DayState.PLANNED)
                .tours(List.of())
                .reference("J131G")
                .build();

        //when
        when(dayComponent.getDayById(day.getReference())).thenReturn(day);
        when(dayComponent.updateDayState(day.getReference(),DayState.COMPLETED)).thenThrow(new UpdateDayStateException("Cannot update current state!",DayState.PLANNED));

        //then
        assertThrows(UpdateDayStateRestException.class,()->dayService.updateDayState(day.getReference(),DayState.COMPLETED));

    }

    @Test
    void getSetUpBundleNotOK(){
        Address a1 = new Address("21 rue de la paix","38000","Grenoble");
        Address a2 = new Address("21 rue de la joie","38000","Grenoble");
        WarehouseEntity warehouse=WarehouseEntity.builder().name("Grenis").coordinates(new Coordinates(12.76,14.874)).build();
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

        TruckEntity t1 = TruckEntity.builder()
                .immatriculation("ABC")
                .build();
        TruckEntity t2 = TruckEntity.builder()
                .immatriculation("DEF")
                .build();

        warehouse.setTrucks(Set.of(t1,t2));
        Set<String> ref = new LinkedHashSet<>();
        ref.add(o1.getReference());
        ref.add(o2.getReference());
        MultipleOrder m1 = new MultipleOrder(ref,a1.toString());
        MultipleOrder m2 = new MultipleOrder(Set.of(o3.getReference()),a2.toString());
        LinkedHashSet<MultipleOrder> m3 = new LinkedHashSet<>();
        m3.add(m1);
        m3.add(m2);

        Set<String> employeeIds = new HashSet<>();
        employeeIds.add(e1.getTrigram());
        employeeIds.add(e2.getTrigram());
        employeeIds.add(e3.getTrigram());

        when(employeeComponent.getAllDeliveryMenID((warehouse.getName()))).thenReturn(employeeIds);
        when(warehouseComponent.getAllTrucks("Paris")).thenThrow(new WarehouseNotFoundException(""));
        when(warehouseComponent.getWarehouseCoordinates(warehouse.getName())).thenReturn(warehouse.getCoordinates());
        when(orderComponent.createMultipleOrders()).thenReturn(m3);


        assertThrows(WarehouseNotFoundRestException.class,()->dayService.getSetUpBundle("Paris"));
    }
}





