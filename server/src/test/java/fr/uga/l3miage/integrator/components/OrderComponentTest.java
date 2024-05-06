package fr.uga.l3miage.integrator.components;


import fr.uga.l3miage.integrator.datatypes.Address;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.CustomerEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import fr.uga.l3miage.integrator.responses.datatypes.MultipleOrder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureTestDatabase
public class OrderComponentTest {

    @Autowired
    private OrderComponent orderComponent;

    @MockBean
    private OrderRepository orderRepository;




    @Test
    void createMultipleOrders(){

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
                .creationDate(LocalDate.of(2019, 1, 8))
                .state(OrderState.DELIVERED)
                .customer(c2)
                .build();


        Set<OrderEntity> orderEntities = new HashSet<>();
        orderEntities.add(o1);
        orderEntities.add(o3);
        orderEntities.add(o2);

        when(orderRepository.findOrderEntitiesByStateOrderByCreationDateAsc(OrderState.OPENED)).thenReturn(orderEntities);
        Set<MultipleOrder> multipleOrderSet = orderComponent.createMultipleOrders();

        assertThat(multipleOrderSet.size()).isEqualTo(2);
        assertThat(multipleOrderSet.stream().findFirst().get().getAddress()).isEqualTo(a1.toString());
    }
}
