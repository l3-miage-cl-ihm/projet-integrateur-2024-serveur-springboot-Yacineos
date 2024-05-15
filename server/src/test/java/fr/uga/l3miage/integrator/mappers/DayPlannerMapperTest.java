package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.components.DeliveryComponent;
import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.datatypes.Address;

import fr.uga.l3miage.integrator.datatypes.Coordinates;
import fr.uga.l3miage.integrator.enums.*;
import fr.uga.l3miage.integrator.exceptions.technical.InvalidInputValueException;
import fr.uga.l3miage.integrator.mappers.utils.DayPlannerMapperUtils;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.DeliveryPlannerResponseDTO;
import fr.uga.l3miage.integrator.responses.TourPlannerResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DayPlannerMapperTest {

    @Autowired
    private  DayPlannerMapper dayPlannerMapper;
    @MockBean
    private EmployeeRepository employeeRepository;
    @MockBean
    private TourPlannerMapper tourPlannerMapper;
    @MockBean
    private DeliveryPlannerMapper deliveryPlannerMapper;
    @MockBean
    private TourComponent tourComponent;
    @MockBean
    private DeliveryComponent deliveryComponent;

    @Autowired
    private DayPlannerMapperUtils dayPlannerMapperUtils;

    @Test
    void dayCreationRequest_To_DayEntity() throws InvalidInputValueException {
        //given
        LocalDate now=LocalDate.of(2024,05,15);
        TruckEntity truck = TruckEntity.builder().immatriculation("WT-543-TR").build();
        OrderEntity order1= OrderEntity.builder().state(OrderState.PLANNED).reference("c001").build();
        OrderEntity order2= OrderEntity.builder().state(OrderState.PLANNED).reference("c002").build();
        DeliveryCreationRequest deliveryCreationRequest=DeliveryCreationRequest.builder()
                .orders(Set.of(order1.getReference(),order2.getReference()))
                .coordinates(List.of(23.7,12.9))
                .distanceToCover(23).build();
        EmployeeEntity deliveryman1=EmployeeEntity.builder().job(Job.DELIVERYMAN).trigram("JLY").mobilePhone("0876342324").build();
        EmployeeEntity deliveryman2=EmployeeEntity.builder().job(Job.DELIVERYMAN).trigram("PLE").mobilePhone("0876342654").build();
        TourCreationRequest tourCreationRequest = TourCreationRequest.builder()
                .distanceToCover(49)
                .truck(truck.getImmatriculation())
                .deliverymen(Set.of(deliveryman1.getTrigram(),deliveryman2.getTrigram()))
                .deliveries(List.of(deliveryCreationRequest))
                .build();


        WarehouseEntity grenis =WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards","65001","San antonio")).trucks(Set.of(truck)).build();

        EmployeeEntity planner = EmployeeEntity.builder().email("claudiatessier@gmail.com").job(Job.PLANNER).photo("chris.png")
                .lastName("TESSIERE").firstName("claudia").mobilePhone("0765437876").trigram("STR").warehouse(grenis).build();

        DayCreationRequest dayCreationRequest = DayCreationRequest.builder().date(now).tours(List.of(tourCreationRequest)).build();
        TourEntity expectedResponse= TourEntity.builder().reference("t136G-A").state(TourState.PLANNED).distanceToCover(49).letter("A").deliveries(new LinkedList<>()).deliverymen(Set.of(deliveryman1,deliveryman2)).truck(truck).build();
        DeliveryEntity  expectedDeliveryResponse= DeliveryEntity.builder().state(DeliveryState.PLANNED).orders(Set.of(order1,order2)).reference("l136G-A1").build();
        //when
        when(employeeRepository.findById(planner.getTrigram())).thenReturn(Optional.of(planner));
        when(tourComponent.generateTourReference(any(LocalDate.class),any(Integer.class))).thenReturn(expectedResponse.getReference());
        when(tourPlannerMapper.toEntity(any(TourCreationRequest.class),anyString())).thenReturn(expectedResponse);
        when(deliveryPlannerMapper.toEntity(any(DeliveryCreationRequest.class),anyString())).thenReturn(expectedDeliveryResponse);
        doNothing().when(deliveryComponent).saveDelivery(any(DeliveryEntity.class));
        doNothing().when(tourComponent).saveTour(any(TourEntity.class));

        DayEntity expectedDayResponse= DayEntity.builder().reference(dayPlannerMapperUtils.generateDayReference(now)).state(DayState.PLANNED).date(now).planner(planner).tours(List.of(expectedResponse)).build();
        DayEntity dayEntityResponse=dayPlannerMapper.toEntity(dayCreationRequest);

        //then
        assertThat(dayEntityResponse.getReference()).isEqualTo(expectedDayResponse.getReference());
        assertThat(dayEntityResponse.getPlanner().getEmail()).isEqualTo(expectedDayResponse.getPlanner().getEmail());
    }

    @Test
    void toResponseTest(){
        // creation deliveryMen 1
        List<TourEntity> tours= new LinkedList<>();
        Set<EmployeeEntity> deliverymen= new HashSet<>();
        WarehouseEntity warehouse=WarehouseEntity.builder().coordinates(new Coordinates(23.74,34.8)).days(Set.of()).address(new Address("23 rue","34000","Londre")).build();
        EmployeeEntity m1=EmployeeEntity.builder().trigram("jjo").warehouse(warehouse).email("jojo@gmail.com").build();
        EmployeeEntity m2=EmployeeEntity.builder().trigram("axl").warehouse(warehouse).email("axel@gmail.com").build();

        deliverymen.add(m1);
        deliverymen.add(m2);
        //Creation delivery 1
        //creation order
        //creation customers
        CustomerEntity customer1=CustomerEntity.builder().address(new Address("2 rue de paris","76000","Ohio")).firstName("Rony").lastName("Diiiirect").build();
        CustomerEntity customer2=CustomerEntity.builder().address(new Address("16 rue de caen","34650","Caen")).firstName("Jug").lastName("Diiiirectman").build();
        CustomerEntity customer3=CustomerEntity.builder().address(new Address("02 rue de toulon","10002","Toulon")).firstName("mely").lastName("joie").build();
        CustomerEntity customer4=CustomerEntity.builder().address(new Address("02 rue de marseille","13003","Marseille")).firstName("melyssa").lastName("bondy").build();

        OrderEntity order11=OrderEntity.builder().reference("c11").customer(customer1).build();
        OrderEntity order12=OrderEntity.builder().reference("c12").customer(customer1).build();
        DeliveryEntity del1=DeliveryEntity.builder().coordinates(new Coordinates(23.74,34.8)).reference("T238G-A1").distanceToCover(1.0).build();
        del1.setOrders(Set.of(order11,order12));

        //Creation delivery 2
        //creation order
        OrderEntity order21=OrderEntity.builder().reference("c21").customer(customer2).build();
        OrderEntity order22=OrderEntity.builder().reference("c22").customer(customer2).build();

        DeliveryEntity del2=DeliveryEntity.builder().coordinates(new Coordinates(23.74,34.8)).reference("T238G-A2").distanceToCover(2.0).build();
        del2.setOrders(Set.of(order21,order22));

        List<DeliveryEntity> deliveries1=new LinkedList<>();
        deliveries1.add(del1);
        deliveries1.add(del2);
        //creation tour 1
        TruckEntity truck1=TruckEntity.builder().immatriculation("EV-666-IL").build();
        TourEntity tour1= TourEntity.builder().reference("T238G-A").distanceToCover(0.0).truck(truck1).deliveries(deliveries1).deliverymen(deliverymen).build();


        tours.add(tour1);
        // creation deliveryMen 2
        Set<EmployeeEntity> deliverymen2= new HashSet<>();
        EmployeeEntity m3=EmployeeEntity.builder().trigram("jju").warehouse(warehouse).email("juju@gmail.com").build();
        EmployeeEntity m4=EmployeeEntity.builder().trigram("alx").warehouse(warehouse).email("alexis@gmail.com").build();

        deliverymen2.add(m3);
        deliverymen2.add(m4);
        //Creation delivery 3
        //creation order
        OrderEntity order31=OrderEntity.builder().reference("c31").customer(customer3).build();
        OrderEntity order32=OrderEntity.builder().reference("c32").customer(customer3).build();

        DeliveryEntity del3=DeliveryEntity.builder().coordinates(new Coordinates(23.74,34.8)).reference("T238G-B1").distanceToCover(3.0).build();
        del3.setOrders(Set.of(order31,order32));

        //Creation delivery 4
        //creation order
        OrderEntity order41=OrderEntity.builder().reference("c41").customer(customer4).build();
        OrderEntity order42=OrderEntity.builder().reference("c42").customer(customer4).build();


        DeliveryEntity del4=DeliveryEntity.builder().coordinates(new Coordinates(23.74,34.8)).reference("T238G-B2").distanceToCover(4.0).build();
        del4.setOrders(Set.of(order41,order42));

        List<DeliveryEntity> deliveries2=new LinkedList<>();
        deliveries2.add(del3);
        deliveries2.add(del4);
        //creation tour 2
        TruckEntity truck2=TruckEntity.builder().immatriculation("AB-345-CD").build();

        TourEntity tour2= TourEntity.builder().reference("T238G-B").distanceToCover(12.2).build();
        tour2.setDeliverymen(deliverymen2);
        tour2.setTruck(truck2);
        tour2.setDeliveries(deliveries2);

        tours.add(tour2);

        // creation day
        DayEntity day = DayEntity.builder().reference("J238").date(LocalDate.of(2024,4,29)).build();
        day.setTours(tours);


        DeliveryPlannerResponseDTO dDTO1=new DeliveryPlannerResponseDTO();
        dDTO1.setDistanceToCover(1.0);
        dDTO1.setOrders(Set.of("c11","c12"));
        dDTO1.setAddress("2 rue la belle,"+"Ohio");

        DeliveryPlannerResponseDTO dDTO2= new DeliveryPlannerResponseDTO();
        dDTO2.setDistanceToCover(2.0);
        dDTO2.setOrders(Set.of("c21","c22"));
        dDTO2.setAddress("16 rue de caen,"+"Caen");
        DeliveryPlannerResponseDTO dDTO3=new DeliveryPlannerResponseDTO();
        dDTO3.setDistanceToCover(3.0);
        dDTO3.setOrders(Set.of("c31","c32"));
        dDTO3.setAddress("02 rue de toulon,"+"Toulon");
        DeliveryPlannerResponseDTO dDTO4=new DeliveryPlannerResponseDTO();
        dDTO4.setDistanceToCover(4.0);
        dDTO4.setOrders(Set.of("c41","c42"));
        dDTO4.setAddress("02 rue de marseille,"+"Marseille");

        TourPlannerResponseDTO tDTO1=new TourPlannerResponseDTO();
               tDTO1.setTruck("EV-666-IL");
               tDTO1 .setDeliverymen(Set.of("jjo","axl"));
               tDTO1 .setDistanceToCover(0.0);
               tDTO1 .setRefTour("T238G-A");
        List<DeliveryPlannerResponseDTO> s1=new ArrayList<>();
        s1.add(dDTO1);
        s1.add(dDTO2);
        tDTO1.setDeliveries(s1);


        TourPlannerResponseDTO tDTO2=new TourPlannerResponseDTO();
        tDTO2.setTruck("AB-345-CD");
        tDTO2 .setDeliverymen(Set.of("jju","alx"));
        tDTO2 .setDistanceToCover(12.2);
        tDTO2 .setRefTour("T238G-B");

        List<DeliveryPlannerResponseDTO> s2=new ArrayList<>();
        s2.add(dDTO3);
        s2.add(dDTO4);
        tDTO2.setDeliveries(s2);



        DayResponseDTO expectedResponse = new DayResponseDTO();
        expectedResponse.setDate(LocalDate.of(2024,4,29));
        List<TourPlannerResponseDTO> s=new LinkedList<>();
        s.add(tDTO1);
        s.add(tDTO2);
        expectedResponse.setTours(s);

        //when
        when(tourPlannerMapper.toResponse(tour1)).thenReturn(tDTO1);
        when(tourPlannerMapper.toResponse(tour2)).thenReturn(tDTO2);
        when(deliveryPlannerMapper.toResponse(del1)).thenReturn(dDTO1);
        when(deliveryPlannerMapper.toResponse(del2)).thenReturn(dDTO2);
        when(deliveryPlannerMapper.toResponse(del3)).thenReturn(dDTO3);
        when(deliveryPlannerMapper.toResponse(del4)).thenReturn(dDTO4);

        DayResponseDTO response = dayPlannerMapper.toResponse(day);

        assertThat(response.getDate()).isEqualTo(expectedResponse.getDate());

    }

}
