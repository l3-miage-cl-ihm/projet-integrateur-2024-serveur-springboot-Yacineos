package fr.uga.l3miage.integrator.components;


import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.CustomerEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import fr.uga.l3miage.integrator.responses.datatypes.MultipleOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class OrderComponent {
    private final OrderRepository orderRepository;


    public LinkedHashSet<MultipleOrder> createMultipleOrders() {
        // Find all opened orders
        Set<OrderEntity> allOrder = orderRepository.findOrderEntitiesByStateOrderByCreationDateAsc(OrderState.OPENED);
        LinkedHashSet<OrderEntity> allOrders = new LinkedHashSet<>();
        allOrder.stream().limit(30).forEach(allOrders::add);
        // group orders by customer
        Map<CustomerEntity, List<OrderEntity>> ordersByCustomer = allOrders.stream()
                .collect(Collectors.groupingBy(OrderEntity::getCustomer));

        LinkedHashSet<MultipleOrder> multipleOrders = new LinkedHashSet<>();

        // Parcourir chaque entrée dans la map (chaque client et ses commandes)
        for (Map.Entry<CustomerEntity, List<OrderEntity>> entry : ordersByCustomer.entrySet()) {
            CustomerEntity customer = entry.getKey();
            List<OrderEntity> orders = entry.getValue();

            // Create a new List reference of orders of the customer
            Set<String> references = orders.stream()
                    .map(OrderEntity::getReference)
                    .collect(Collectors.toSet());

            // Create a Multiple order to that customer with its orders and address
            MultipleOrder multipleOrder = new MultipleOrder(references, customer.getAddress().toString());

            // Add the previous multiple order to the list
            multipleOrders.add(multipleOrder);
        }

        return multipleOrders;
    }


    }
