package fr.uga.l3miage.integrator.repositories;

import fr.uga.l3miage.integrator.models.TourEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TourRepository extends JpaRepository<TourEntity,String>{

}
