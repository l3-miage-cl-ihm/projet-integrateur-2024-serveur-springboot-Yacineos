package fr.uga.l3miage.integrator.services;

import fr.uga.l3miage.integrator.components.DayComponent;
import fr.uga.l3miage.integrator.components.TourComponent;
import fr.uga.l3miage.integrator.exceptions.rest.EntityNotFoundRestException;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.mappers.*;
import fr.uga.l3miage.integrator.models.*;
import fr.uga.l3miage.integrator.responses.DayResponseDTO;
import fr.uga.l3miage.integrator.responses.TourDMResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DayServiceTest {
    @Autowired
    private DayService dayService;
    @MockBean
    private DayComponent dayComponent;
    @SpyBean
    private DayPlannerMapper dayPlannerMapper;
    @SpyBean
    private TourPlannerMapper tourPlannerMapper;
    @SpyBean
    private DeliveryPlannerMapper deliveryPlannerMapper;


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


}
