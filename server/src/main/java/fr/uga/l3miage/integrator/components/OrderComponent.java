package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.dataTypes.MultipleOrder;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.*;


@Component
@RequiredArgsConstructor
public class OrderComponent {
    private final OrderRepository orderRepository;

    public Set<MultipleOrder> createMultipleOrders() {
        Set<OrderEntity> allOrders = orderRepository.findOrderEntitiesByStateOrderByCreationDateAsc(OrderState.OPENED);
        List<OrderEntity> ordersList = new ArrayList<>(allOrders);
        Set<MultipleOrder> multipleOrders = new HashSet<>();

        if (!ordersList.isEmpty()) {
            Map<String, Set<String>> addressToOrdersMap = new HashMap<>();

            for (OrderEntity order : ordersList) {
                String address = order.getCustomer().getAddress().toString();
                String reference = order.getReference();

                addressToOrdersMap.computeIfAbsent(address, k -> new HashSet<>()).add(reference);
            }

            for (Map.Entry<String, Set<String>> entry : addressToOrdersMap.entrySet()) {
                MultipleOrder multipleOrder = new MultipleOrder();
                multipleOrder.setAddress(entry.getKey());
                multipleOrder.setOrders(entry.getValue());
                multipleOrders.add(multipleOrder);
            }
        }

        return multipleOrders;
    }

}
