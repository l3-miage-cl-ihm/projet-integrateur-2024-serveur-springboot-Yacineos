package fr.uga.l3miage.integrator.components;


import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.CustomerEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import fr.uga.l3miage.integrator.responses.datatypes.MultipleOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)

public class OrderComponentTest {

    @Autowired
    private OrderComponent orderComponent;

    @MockBean
    private OrderRepository orderRepository;

    @Test
    void createMultipleOrders(){
        Address a1 = new Address("21 rue de la paix","38000","Grenoble");
        Address a2 = new Address("21 rue de la joie","38000","Grenoble");
        Address a3 = new Address("ça commence à bien faire","38000","Paris");
        CustomerEntity c1 = CustomerEntity.builder()
                .email("ezfzfzef")
                .address(a1)
                .build();
        CustomerEntity c2 = CustomerEntity.builder()
                .email("zefgzeoi^fze")
                .address(a2)
                .build();
        CustomerEntity c3 = CustomerEntity.builder()
                .email("azdadadfcc")
                .address(a3)
                .build();
        OrderEntity o1 = OrderEntity.builder()
                .reference("c01")
                .creationDate(LocalDate.of(2020, 1, 7))
                .customer(c1)
                .state(OrderState.OPENED)
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
                .state(OrderState.OPENED)
                .customer(c2)
                .build();
        OrderEntity o4 = OrderEntity.builder()
                .reference("c04")
                .creationDate(LocalDate.of(2024, 2, 8))
                .state(OrderState.OPENED)
                .customer(c1)
                .build();

        when(orderRepository.findById(o1.getReference())).thenReturn(Optional.of(o1));
        when(orderRepository.findById(o2.getReference())).thenReturn(Optional.of(o2));
        when(orderRepository.findById(o3.getReference())).thenReturn(Optional.of(o3));
        when(orderRepository.findById(o3.getReference())).thenReturn(Optional.of(o4));
        when(orderRepository.findOrderEntitiesByStateOrderByCreationDateAsc(OrderState.OPENED)).thenReturn(Set.of(o1,o2,o3,o4));
        Set<MultipleOrder> multipleOrderSet = orderComponent.createMultipleOrders();
        assertThat(multipleOrderSet.size()).isEqualTo(2);
    }
}
