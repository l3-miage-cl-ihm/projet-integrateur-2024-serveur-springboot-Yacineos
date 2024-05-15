package fr.uga.l3miage.integrator.exceptions.technical;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class DayNotFoundException extends  Exception {
    public DayNotFoundException(String message) {
        super(message);

    }
}



