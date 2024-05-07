package fr.uga.l3miage.integrator.repositories;

import fr.uga.l3miage.integrator.models.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity,String> {
}
