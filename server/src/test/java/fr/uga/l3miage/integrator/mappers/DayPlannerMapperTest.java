package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.DeliveryPlannerResponseDTO;
import fr.uga.l3miage.integrator.responses.TourPlannerResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.CustomerState;
import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.mappers.utils.DayPlannerMapperUtils;
import fr.uga.l3miage.integrator.mappers.utils.TourDMMapperUtils;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DayPlannerMapperTest {

    @Autowired
    private  DayPlannerMapper dayPlannerMapper;

    @Autowired
    private DayPlannerMapperUtils dayPlannerMapperUtils;
  
    
   @Test
    void toResponseTest(){
        // creation deliveryMen 1
        Set<TourEntity> tours= new HashSet<>();
        Set<EmployeeEntity> deliverymen= new HashSet<>();
        EmployeeEntity m1=EmployeeEntity.builder().trigram("jjo").email("jojo@gmail.com").build();
        EmployeeEntity m2=EmployeeEntity.builder().trigram("axl").email("axel@gmail.com").build();

        deliverymen.add(m1);
        deliverymen.add(m2);
        //Creation delivery 1
        //creation order
        OrderEntity order11=OrderEntity.builder().reference("c11").build();
        OrderEntity order12=OrderEntity.builder().reference("c12").build();

        Set<OrderEntity> orders1 = new HashSet<>();
        orders1.add(order11);
        orders1.add(order12);
        DeliveryEntity del1=DeliveryEntity.builder().reference("T238G-A1").distanceToCover(1.0).build();
        del1.setOrders(orders1);

        //Creation delivery 2
        //creation order
        OrderEntity order21=OrderEntity.builder().reference("c21").build();
        OrderEntity order22=OrderEntity.builder().reference("c22").build();

        Set<OrderEntity> orders2 = new HashSet<>();
        orders2.add(order21);
        orders2.add(order22);
        DeliveryEntity del2=DeliveryEntity.builder().reference("T238G-A2").distanceToCover(2.0).build();
        del2.setOrders(orders2);

        Set<DeliveryEntity> deliveries1=new HashSet<>();
        deliveries1.add(del1);
        deliveries1.add(del2);
        //creation tour 1
        TruckEntity truck1=TruckEntity.builder().immatriculation("EV-666-IL").build();

        TourEntity tour1= TourEntity.builder().reference("T238G-A").distanceToCover(0.0).build();
        tour1.setDeliverymen(deliverymen);
        tour1.setTruck(truck1);
        tour1.setDeliveries(deliveries1);

        tours.add(tour1);
        // creation deliveryMen 2
        Set<EmployeeEntity> deliverymen2= new HashSet<>();
        EmployeeEntity m3=EmployeeEntity.builder().trigram("jju").email("juju@gmail.com").build();
        EmployeeEntity m4=EmployeeEntity.builder().trigram("alx").email("alexis@gmail.com").build();

        deliverymen2.add(m3);
        deliverymen2.add(m4);
        //Creation delivery 3
        //creation order
        OrderEntity order31=OrderEntity.builder().reference("c31").build();
        OrderEntity order32=OrderEntity.builder().reference("c32").build();

        Set<OrderEntity> orders3 = new HashSet<>();
        orders3.add(order31);
        orders3.add(order32);
        DeliveryEntity del3=DeliveryEntity.builder().reference("T238G-B1").distanceToCover(3.0).build();
        del3.setOrders(orders3);

        //Creation delivery 4
        //creation order
        OrderEntity order41=OrderEntity.builder().reference("c41").build();
        OrderEntity order42=OrderEntity.builder().reference("c42").build();

        Set<OrderEntity> orders4 = new HashSet<>();
        orders4.add(order41);
        orders4.add(order42);
        DeliveryEntity del4=DeliveryEntity.builder().reference("T238G-B2").distanceToCover(4.0).build();
        del4.setOrders(orders4);

        Set<DeliveryEntity> deliveries2=new HashSet<>();
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
        DeliveryPlannerResponseDTO dDTO2=new DeliveryPlannerResponseDTO();
        dDTO2.setDistanceToCover(2.0);
        dDTO2.setOrders(Set.of("c21","c22"));
        DeliveryPlannerResponseDTO dDTO3=new DeliveryPlannerResponseDTO();
        dDTO3.setDistanceToCover(3.0);
        dDTO3.setOrders(Set.of("c31","c32"));
        DeliveryPlannerResponseDTO dDTO4=new DeliveryPlannerResponseDTO();
        dDTO4.setDistanceToCover(4.0);
        dDTO4.setOrders(Set.of("c41","c42"));


        TourPlannerResponseDTO tDTO1=new TourPlannerResponseDTO();
        tDTO1.setTruck("EV-666-IL");
        tDTO1.setDeliverymen(Set.of("jjo","axl"));
        tDTO1.setDistanceToCover(0.0);
        tDTO1.setRefTour("T238G-A");
        tDTO1.setDeliveries(Set.of(dDTO2,dDTO1));

        TourPlannerResponseDTO tDTO2=new TourPlannerResponseDTO();
        tDTO2.setTruck("AB-345-CD");
        tDTO2.setDeliverymen(Set.of("jju","alx"));
        tDTO2.setDistanceToCover(12.2);
        tDTO2.setRefTour("T238G-B");
        tDTO2.setDeliveries(Set.of(dDTO4,dDTO3));



        DayResponseDTO expectedResponse = new DayResponseDTO();
        expectedResponse.setDate(LocalDate.of(2024,4,29));
        expectedResponse.setTours(Set.of(tDTO2,tDTO1));

        DayResponseDTO response = dayPlannerMapper.toResponse(day);

        assertThat(response).usingRecursiveComparison().isEqualTo(expectedResponse);


    }

    @Test
    void dayCreationReuest_To_DayEntity(){
        //given
        LocalDate now = LocalDate.now();
        TourCreationRequest tour1 = TourCreationRequest.builder().distanceToCover(40).build();
        DayCreationRequest dayCreationRequest = DayCreationRequest.builder().date(LocalDate.now()).tours(Set.of(tour1)).build();


        WarehouseEntity grenis =WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards","65001","San antonio")).trucks(Set.of()).build();

        EmployeeEntity planner = EmployeeEntity.builder().email("christian.paul@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("Paul").firstName("Christian").mobilePhone("0765437876").trigram("CPL").warehouse(grenis).build();

        DayEntity expectedResponse= DayEntity.builder().reference(dayPlannerMapperUtils.generateDayReference(LocalDate.now())).state(DayState.PLANNED).date(now).planner(planner).tours(Set.of()).build();
        DayEntity dayEntityResponse=dayPlannerMapper.toEntity(dayCreationRequest);

        //then
        assertThat(dayEntityResponse.getReference()).isEqualTo(expectedResponse.getReference());
        assertThat(dayEntityResponse.getPlanner().getEmail()).isEqualTo(expectedResponse.getPlanner().getEmail());
    }
  


}
