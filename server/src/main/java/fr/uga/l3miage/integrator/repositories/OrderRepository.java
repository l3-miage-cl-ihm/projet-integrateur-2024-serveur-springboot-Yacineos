package fr.uga.l3miage.integrator.repositories;


import fr.uga.l3miage.integrator.enums.OrderState;
import fr.uga.l3miage.integrator.models.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,String> {
     Set<OrderEntity> findOrderEntitiesByStateOrderByCreationDateAsc(OrderState orderState);

}
