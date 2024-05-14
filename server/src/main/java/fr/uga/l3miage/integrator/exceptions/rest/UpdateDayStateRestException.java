package fr.uga.l3miage.integrator.exceptions.rest;


import fr.uga.l3miage.integrator.enums.DayState;
import lombok.Getter;

@Getter
public class UpdateDayStateRestException extends RuntimeException {
    private final DayState dayState;

    public UpdateDayStateRestException(String message, DayState dayState) {
        super(message);
        this.dayState = dayState;
    }

}
