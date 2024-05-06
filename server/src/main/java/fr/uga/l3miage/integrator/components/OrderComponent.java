package fr.uga.l3miage.integrator.components;


import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.OrderEntity;
import fr.uga.l3miage.integrator.repositories.OrderRepository;
import fr.uga.l3miage.integrator.responses.datatypes.MultipleOrder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.*;


@Component
@RequiredArgsConstructor
public class OrderComponent {
    private final OrderRepository orderRepository;
    public Set<MultipleOrder> createMultipleOrders() {
        Set<OrderEntity> allOrders = orderRepository.findOrderEntitiesByStateOrderByCreationDateAsc(OrderState.OPENED);
        Set<MultipleOrder> multipleOrders =  new LinkedHashSet<>();
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


    }
