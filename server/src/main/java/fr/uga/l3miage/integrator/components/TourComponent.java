package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.repositories.DayRepository;
import fr.uga.l3miage.integrator.repositories.TourRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TourComponent {

    private final DayRepository dayRepository;
    private final TourRepository tourRepository;

    public TourEntity getTourOfTheDay(String email) throws DayNotFoundException, TourNotFoundException {
        DayEntity today= dayRepository.findByDate(LocalDate.now()).orElseThrow(()-> new DayNotFoundException("No day was planned for today : "+ LocalDate.now()));  //get the current day

        return today
                .getTours()
                .stream()
                .filter(tourEntity -> tourEntity.getDeliverymen()
                                .stream()
                                .anyMatch(deliverymen-> deliverymen.getEmail().equals(email))
                )
                .findFirst()
                .orElseThrow(()-> new TourNotFoundException("No tour was found for <"+email+">"));



    }


    public void saveTour(TourEntity tour ){
        tourRepository.save(tour);
    }


    public String generateTourReference(LocalDate date, int tourIndex) {
        String dayNumber = String.format("%03d", date.getDayOfYear());
        char letter = (char) ('A' + tourIndex);
        return "t" + dayNumber + "-G" + letter;
    }
}
