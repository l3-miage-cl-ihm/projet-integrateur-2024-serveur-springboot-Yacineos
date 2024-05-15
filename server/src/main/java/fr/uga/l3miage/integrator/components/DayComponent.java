package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.enums.DayState;
import fr.uga.l3miage.integrator.enums.TourState;
import fr.uga.l3miage.integrator.exceptions.technical.DayNotFoundException;
import fr.uga.l3miage.integrator.exceptions.technical.UpdateDayStateException;
import fr.uga.l3miage.integrator.models.DayEntity;
import fr.uga.l3miage.integrator.models.TourEntity;
import fr.uga.l3miage.integrator.repositories.DayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DayComponent {
    private final DayRepository dayRepository;

    public DayEntity getDay(LocalDate date) throws DayNotFoundException {
        return dayRepository.findByDate(date).orElseThrow(()-> new DayNotFoundException("No day found for the " + date.toString()));
    }


    public void planDay(DayEntity day) {
        dayRepository.save(day);
    }

    public boolean isDayAlreadyPlanned(LocalDate date) {
        Optional<DayEntity> dayEntity = dayRepository.findByDate(date);
        return dayEntity.isPresent();
    }

    public DayEntity getDayById(String dayId) throws DayNotFoundException {
        return dayRepository.findById(dayId).orElseThrow(() -> new DayNotFoundException("Cannot find day with given id  <" + dayId + "> !"));
    }

    public DayEntity updateDayState(String dayId, DayState newDayState) throws DayNotFoundException, UpdateDayStateException {
        DayEntity dayEntity = this.getDayById(dayId);
        DayState currentState = dayEntity.getState();

        switch (currentState) {
            case PLANNED:
                if (newDayState == DayState.IN_PROGRESS) {
                    dayEntity.setState(newDayState);
                } else {
                    throw new UpdateDayStateException("Cannot switch from " + currentState + " into <" + newDayState + ">", dayEntity.getState());
                }
                break;

            case IN_PROGRESS:
                List<TourEntity> tours = dayEntity.getTours();
                if (newDayState == DayState.COMPLETED && tours.stream().allMatch(tour -> tour.getState() == TourState.COMPLETED)) {
                    dayEntity.setState(newDayState);
                } else {
                    throw new UpdateDayStateException("Cannot switch from " + currentState + " into <" + newDayState + "> make sure that all tours are completed !", dayEntity.getState());
                }
                break;

            default:
                throw new UpdateDayStateException("Day is already completed !", dayEntity.getState());

        }


        return dayRepository.save(dayEntity);
    }

    public void deleteDay(String dayRef) {
        dayRepository.deleteById(dayRef);
    }

}
