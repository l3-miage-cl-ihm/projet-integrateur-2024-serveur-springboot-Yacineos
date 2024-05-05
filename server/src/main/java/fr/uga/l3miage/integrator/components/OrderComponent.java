package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.dataTypes.MultipleOrder;
import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderComponent {
    private final OrderRepository orderRepository;



//    public Set<MultipleOrder> createMultipleOrders() {
//        Set<OrderEntity> allOrders = orderRepository.findOrderEntitiesByStateOrderByCreationDateAsc(OrderState.OPENED);
//        Set<OrderEntity> orders=allOrders.stream().limit(30).collect(Collectors.toSet());
//        List<OrderEntity> ordersList = new ArrayList<>(orders);
//        Set<MultipleOrder> m1 = new HashSet<>();
//        if (!orders.isEmpty()) {
//            MultipleOrder multipleOrder = new MultipleOrder();
//            Set<String> orderEntities = new HashSet<>();
//            multipleOrder.setAddress(ordersList.get(0).getCustomer().getAddress().toString());
//            multipleOrder.setOrders(Set.of(ordersList.get(0).getReference()));
//            orderEntities.add(ordersList.get(0).getReference());
//
//            for (int i = 1; i < ordersList.size(); i++) {
//                if (multipleOrder.getAddress().equals(ordersList.get(i).getCustomer().getAddress().toString())) {
//                    orderEntities.add(ordersList.get(i).getReference());
//                    multipleOrder.setOrders(new HashSet<>(orderEntities));
//                } else {
//                    m1.add(new MultipleOrder(new HashSet<>(orderEntities), multipleOrder.getAddress()));
//                    multipleOrder = new MultipleOrder(); // Créer un nouveau MultipleOrder
//                    multipleOrder.setAddress(ordersList.get(i).getCustomer().getAddress().toString());
//                    orderEntities = new HashSet<>();
//                    orderEntities.add(ordersList.get(i).getReference());
//                    multipleOrder.setOrders(new HashSet<>(orderEntities));
//                }
//            }
//            m1.add(new MultipleOrder(new HashSet<>(orderEntities), multipleOrder.getAddress()));
//        }
//
//
//        return m1;
//    }

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
