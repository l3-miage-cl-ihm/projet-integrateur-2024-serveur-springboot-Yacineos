package fr.uga.l3miage.integrator.repositories;

import fr.uga.l3miage.integrator.models.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity,String> {
}