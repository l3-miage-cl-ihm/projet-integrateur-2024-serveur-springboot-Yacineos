package fr.uga.l3miage.integrator.repositories;

import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.OrderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect")
@AutoConfigureTestDatabase
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void findOrderEntitiesByStateOrderByCreationDateAsc(){

        //given

        OrderEntity o1 = OrderEntity.builder()
                .reference("c01")
                .creationDate(LocalDate.of(2020, 1, 9))
                .state(OrderState.OPENED)
                .build();
        OrderEntity o2 = OrderEntity.builder()
                .reference("c02")
                .creationDate(LocalDate.of(2023, 2, 9))
                .state(OrderState.OPENED)
                .build();
        OrderEntity o3 = OrderEntity.builder()
                .reference("c03")
                .creationDate(LocalDate.of(2020, 1, 8))
                .state(OrderState.OPENED)
                .build();
        OrderEntity o4 = OrderEntity.builder()
                .reference("c04")
                .creationDate(LocalDate.of(2019, 1, 8))
                .state(OrderState.DELIVERED)
                .build();
        orderRepository.save(o1);
        orderRepository.save(o2);
        orderRepository.save(o3);
        orderRepository.save(o4);


        //when
        Set<OrderEntity> response = orderRepository.findOrderEntitiesByStateOrderByCreationDateAsc(OrderState.OPENED);


        //then
        assertThat(response.stream().findFirst().get().getReference()).isEqualTo("c03");
        assertThat(response.size()).isEqualTo(3);



    }
}
