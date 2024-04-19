package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.repositories.DayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TourComponent {

    private final DayRepository dayRepository;

    public TourEntity getTourOfTheDay(String email) throws DayNotFoundException, TourNotFoundException {
        Optional<DayEntity> today= dayRepository.findByDate(LocalDate.now());  //get the current day
        if(today.isPresent()){
            Optional<TourEntity> tour =today.get().getTours().stream().filter(tourEntity -> tourEntity.getDeliverymen().stream().anyMatch(deliverymen-> deliverymen.getEmail().equals(email))).findFirst();
            if(tour.isPresent()){
                return tour.get();
            }else {
                throw new TourNotFoundException("No tour was found for <"+email+">");
            }
        }else {
            throw  new DayNotFoundException("No day was planned for today : "+ LocalDate.now());
        }

    }
}
