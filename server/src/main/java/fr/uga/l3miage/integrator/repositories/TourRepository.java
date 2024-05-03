package fr.uga.l3miage.integrator.repositories;

import fr.uga.l3miage.integrator.models.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TourRepository extends JpaRepository<TourEntity,String>{

}
