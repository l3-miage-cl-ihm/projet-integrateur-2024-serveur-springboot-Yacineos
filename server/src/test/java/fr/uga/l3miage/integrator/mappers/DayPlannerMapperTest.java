package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.CustomerState;
import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.enums.Job;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.repositories.*;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import fr.uga.l3miage.integrator.requests.TourCreationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DayPlannerMapperTest {

    @Autowired
    private  DayPlannerMapper dayPlannerMapper;

    @MockBean
    private  TruckRepository truckRepository;
    @MockBean
    private  OrderRepository orderRepository;
    @MockBean
    private  EmployeeRepository employeeRepository;

    @Test
    void dayCreationReuest_To_DayEntity(){
        //goiven
        LocalDate now = LocalDate.now();
        DayCreationRequest dayCreationRequest= DayCreationRequest.builder().date(now).build();
        Set<TourCreationRequest> tours = new HashSet<>();
        TruckEntity truck1= TruckEntity.builder().immatriculation("WY-543-YR").build();
        CustomerEntity customer=CustomerEntity.builder().state(CustomerState.DELIVERABLE).email("john@gmail.com").firstName("john").lastName("thierry").address(new Address("21 rue des amandes","87653","Bali")).build();
        OrderEntity order1= OrderEntity.builder().reference("c200").lines(Set.of()).rate(20).state(OrderState.OPENED).feedback("bon!").creationDate(LocalDate.now()).customer(customer).build();
        OrderEntity order2= OrderEntity.builder().reference("c201").lines(Set.of()).rate(20).state(OrderState.OPENED).feedback("bon bon !").creationDate(LocalDate.now()).customer(customer).build();
        //orderRepository.save(order1);
        //orderRepository.save(order2);

        EmployeeEntity deliveryman1= EmployeeEntity.builder().lastName("Miel").firstName("Nils").email("milen@gmail.com").trigram("NML").job(Job.DELIVERYMAN).mobilePhone("0877654332").build();
        EmployeeEntity deliveryman2= EmployeeEntity.builder().lastName("Jug").firstName("Our").email("jugour@gmail.com").trigram("JOR").job(Job.DELIVERYMAN).mobilePhone("0877654352").build();
        //employeeRepository.save(deliveryman1);
        //employeeRepository.save(deliveryman2);

        DeliveryCreationRequest deliveryCreationRequest1=DeliveryCreationRequest.builder().orders(Set.of(order1.getReference())).distanceToCover(30).build();
        DeliveryCreationRequest deliveryCreationRequest2=DeliveryCreationRequest.builder().orders(Set.of(order2.getReference())).distanceToCover(10).build();



        TourCreationRequest tour1 = TourCreationRequest.builder().truck(truck1.getImmatriculation()).deliveries(Set.of(deliveryCreationRequest1,deliveryCreationRequest2)).deliverymen(Set.of(deliveryman1.getTrigram(),deliveryman2.getTrigram())).distanceToCover(40).build();

        DayCreationRequest dayCreationRequest1 = DayCreationRequest.builder().date(LocalDate.now()).tours(Set.of(tour1)).build();



        //when
        when(truckRepository.findById(truck1.getImmatriculation())).thenReturn(Optional.of(truck1));
        when(orderRepository.findById(order1.getReference())).thenReturn(Optional.of(order1));
        when(orderRepository.findById(order2.getReference())).thenReturn(Optional.of(order2));
        when(employeeRepository.findById(deliveryman1.getTrigram())).thenReturn(Optional.of(deliveryman1));
        when(employeeRepository.findById(deliveryman2.getTrigram())).thenReturn(Optional.of(deliveryman2));


        WarehouseEntity grenis =WarehouseEntity.builder().days(Set.of()).photo("grenis.png").name("Grenis").letter("G")
                .address(new Address("21 rue des cafards","65001","San antonio")).trucks(Set.of()).build();

        EmployeeEntity planner = EmployeeEntity.builder().email("christian.paul@grenis.com").job(Job.PLANNER).photo("chris.png")
                .lastName("Paul").firstName("Christian").mobilePhone("0765437876").trigram("CPL").warehouse(grenis).build();

        DayEntity expectedResponse= DayEntity.builder().reference("J030G").state(DayState.PLANNED).date(now).planner(planner).tours(Set.of()).build();
        DayEntity dayEntityResponse=dayPlannerMapper.toEntity(dayCreationRequest1);


        //then
        assertThat(dayEntityResponse.getReference()).isEqualTo(expectedResponse.getReference());
    }

}
