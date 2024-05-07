package fr.uga.l3miage.integrator.components;


import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.CustomerEntity;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import fr.uga.l3miage.integrator.responses.datatypes.MultipleOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;


@Component
@RequiredArgsConstructor
public class OrderComponent {
    private final OrderRepository orderRepository;
    public LinkedHashSet<MultipleOrder> createMultipleOrdersc() {
        Set<OrderEntity> allOrder = orderRepository.findOrderEntitiesByStateOrderByCreationDateAsc(OrderState.OPENED);
        LinkedHashSet<OrderEntity> allOrders = new LinkedHashSet<>();
        allOrder.stream().limit(30).forEach(allOrders::add);
//        Map<CustomerEntity, List<OrderEntity>> postsPerType = allOrders.stream()
//                .collect(groupingBy(OrderEntity::getCustomer));
        LinkedHashSet<MultipleOrder> multipleOrders =  new LinkedHashSet<>();
        String address = allOrders.stream().findFirst().get().getCustomer().getAddress().toString();
        Set<String> reference = new LinkedHashSet<>();
        reference.add(allOrders.stream().findFirst().get().getReference());
        MultipleOrder multipleOrder = new MultipleOrder(reference,address);
        for(OrderEntity orderEntity: allOrders){
            if(multipleOrder.getAddress().equals(orderEntity.getCustomer().getAddress().toString())){
                reference.add(orderEntity.getReference());
                multipleOrder.setOrders(reference);
            }else{
                multipleOrders.add(multipleOrder);
                address = orderEntity.getCustomer().getAddress().toString();
                reference = new LinkedHashSet<>();
                reference.add(orderEntity.getReference());
                multipleOrder = new MultipleOrder(reference,address);
            }
        }
        multipleOrders.add(multipleOrder);
        return multipleOrders;
    }

    public LinkedHashSet<MultipleOrder> createMultipleOrders() {
        // Récupérer toutes les commandes ouvertes
        Set<OrderEntity> allOrder = orderRepository.findOrderEntitiesByStateOrderByCreationDateAsc(OrderState.OPENED);
        LinkedHashSet<OrderEntity> allOrders = new LinkedHashSet<>();
        allOrder.stream().limit(30).forEach(allOrders::add);
        // Regrouper les commandes par client
        Map<CustomerEntity, List<OrderEntity>> ordersByCustomer = allOrders.stream()
                .collect(Collectors.groupingBy(OrderEntity::getCustomer));

        LinkedHashSet<MultipleOrder> multipleOrders = new LinkedHashSet<>();

        // Parcourir chaque entrée dans la map (chaque client et ses commandes)
        for (Map.Entry<CustomerEntity, List<OrderEntity>> entry : ordersByCustomer.entrySet()) {
            CustomerEntity customer = entry.getKey();
            List<OrderEntity> orders = entry.getValue();

            // Créer une nouvelle liste de références de commandes pour chaque client
            Set<String> references = orders.stream()
                    .map(OrderEntity::getReference)
                    .collect(Collectors.toSet());

            // Créer un MultipleOrder pour ce client avec ses commandes et son adresse
            MultipleOrder multipleOrder = new MultipleOrder(references, customer.getAddress().toString());

            // Ajouter le MultipleOrder à la liste
            multipleOrders.add(multipleOrder);
        }

        return multipleOrders;
    }


    }
