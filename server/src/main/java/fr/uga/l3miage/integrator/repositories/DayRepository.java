package fr.uga.l3miage.integrator.repositories;

import fr.uga.l3miage.integrator.models.DayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DayRepository extends JpaRepository<DayEntity,String> {

    public Optional<DayEntity> findByDate(LocalDate date);
}
