package fr.uga.l3miage.integrator.exceptions.rest;

import lombok.Getter;

@Getter
public class DayCreationRestException extends  RuntimeException{
    public DayCreationRestException(String message){
        super(message);
    }
}
