package fr.uga.l3miage.integrator.mappers;

import fr.uga.l3miage.integrator.enums.DeliveryState;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.exceptions.rest.DayCreationRestException;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import fr.uga.l3miage.integrator.requests.DeliveryCreationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class DeliveryPlannerMapperTest {
    @Autowired
    private DeliveryPlannerMapper deliveryPlannerMapper;
    @MockBean
    private OrderRepository orderRepository;

    @Test
    void DeliveryCreationRequest_to_DeliveryEntityOK(){
        //given
        OrderEntity order1= OrderEntity.builder().state(OrderState.PLANNED).creationDate(LocalDate.now()).reference("c001").build();
        OrderEntity order2= OrderEntity.builder().state(OrderState.PLANNED).creationDate(LocalDate.now()).reference("c002").build();
        DeliveryCreationRequest deliveryCreationRequest=DeliveryCreationRequest.builder().orders(Set.of(order1.getReference(),order2.getReference())).distanceToCover(23).build();

        //when
        when(orderRepository.findById(order1.getReference())).thenReturn(Optional.of(order1));
        when(orderRepository.findById(order2.getReference())).thenReturn(Optional.of(order2));

        DeliveryEntity  expectedResponse= DeliveryEntity.builder().state(DeliveryState.PLANNED).orders(Set.of(order1,order2)).reference("l123G-A1").build();
        DeliveryEntity actualResponse= deliveryPlannerMapper.toEntity(deliveryCreationRequest,"l123G-A1");
        //then
        assertThat(actualResponse.getReference()).isEqualTo(expectedResponse.getReference());


    }
    @Test
    void DeliveryCreationRequest_to_DeliveryEntity_NotOK_BecauseOfNotFoundOrder(){
        //given
        OrderEntity order1= OrderEntity.builder().state(OrderState.PLANNED).creationDate(LocalDate.now()).reference("c001").build();
        DeliveryCreationRequest deliveryCreationRequest=DeliveryCreationRequest.builder().orders(Set.of(order1.getReference(),"c002")).distanceToCover(23).build();

        //when
        when(orderRepository.findById(order1.getReference())).thenReturn(Optional.of(order1));
        when(orderRepository.findById("c002")).thenReturn(Optional.empty());

        //then
        assertThrows(DayCreationRestException.class,()-> deliveryPlannerMapper.toEntity(deliveryCreationRequest,"l123G-A1"));


    }

}
