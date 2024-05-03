package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.repositories.DayRepository;
import fr.uga.l3miage.integrator.mappers.DayPlannerMapper;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.DeliveryEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.repositories.DeliveryRepository;
import fr.uga.l3miage.integrator.repositories.TourRepository;
import fr.uga.l3miage.integrator.requests.DayCreationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class DayComponent {
    private final DayRepository dayRepository;
    private final TourRepository tourRepository;
    private final DeliveryRepository deliveryRepository;
    
    public DayEntity getDay(LocalDate date) throws DayNotFoundException {
        Optional<DayEntity> day=dayRepository.findByDate(date);
        if(day.isPresent()) {
            return day.get();
        }else{
            throw new DayNotFoundException("No day found for the "+date.toString());
        }
    }



    public DayEntity getDay(LocalDate date) {
        return null;
    }

    public void planDay (DayEntity day){
        dayRepository.save(day);
    }

    public boolean isDayAlreadyPlanned(LocalDate date){
        Optional<DayEntity> dayEntity= dayRepository.findByDate(date);
        return dayEntity.isPresent();
    }


}
