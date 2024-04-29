package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.repositories.DayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DayComponent {
    private final DayRepository dayRepository;
    public DayEntity getDay(LocalDate date) throws DayNotFoundException {
        Optional<DayEntity> day=dayRepository.findByDate(date);
        if(day.isPresent()) {
            return day.get();
        }else{
            throw new DayNotFoundException("No day found for the "+date.toString());
        }
    }

}
