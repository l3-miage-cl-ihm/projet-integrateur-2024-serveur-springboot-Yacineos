package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.TourNotFoundException;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.repositories.DayRepository;
import fr.uga.l3miage.integrator.repositories.TourRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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


    // planner able to create 676 tours un a single day at max
    // the format of the reference will change from t132G-A,...,t132G-Y, t132G-Z, to t132G-AA, t132G-AB,....t132G-ZZ
    public String generateTourReference(LocalDate date, int tourIndex) {
        String dayNumber = String.format("%03d", date.getDayOfYear());

        if(tourIndex<26) {
            char letter = (char) ('A' + tourIndex);
            return "t" + dayNumber + "G-" + letter;
        }else{
            int div = tourIndex/26;
            int mod = tourIndex%26;
            char firstLetter = (char) ('A' + div - 1);
            char secondLetter = (char) ('A'+ mod);
            return "t" + dayNumber + "G-" + firstLetter + secondLetter;
        }
    }

    public TourEntity findTourById(String tourId) throws TourNotFoundException {
        return tourRepository.findById(tourId).orElseThrow(()-> new TourNotFoundException("No tour was found !"));
    }


    public void deleteTour(String tourId){
        tourRepository.deleteById(tourId);
    }


}
