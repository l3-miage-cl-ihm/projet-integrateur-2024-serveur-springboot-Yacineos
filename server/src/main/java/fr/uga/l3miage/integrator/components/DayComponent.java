package fr.uga.l3miage.integrator.components;

import fr.uga.l3miage.integrator.models.DayEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DayComponent {

    public DayEntity getDay(LocalDate date) {
        return null;
    }
}
