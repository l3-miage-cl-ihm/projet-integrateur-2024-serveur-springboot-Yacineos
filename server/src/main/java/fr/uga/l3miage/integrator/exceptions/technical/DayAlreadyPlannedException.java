package fr.uga.l3miage.integrator.exceptions.technical;

import lombok.Getter;

@Getter
public class DayAlreadyPlannedException extends Exception{
    public DayAlreadyPlannedException(String message){
        super(message);
    }
}
