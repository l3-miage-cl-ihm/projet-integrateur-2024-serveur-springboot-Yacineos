package fr.uga.l3miage.integrator.exceptions.technical;

import fr.uga.l3miage.integrator.enums.DayState;
import lombok.Getter;

@Getter
public class UpdateDayStateException extends Exception{
    private final DayState dayState;

    public  UpdateDayStateException(String message,DayState dayState){
        super(message);
        this.dayState=dayState;
    }
}
