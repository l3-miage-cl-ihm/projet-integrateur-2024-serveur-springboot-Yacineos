package fr.uga.l3miage.integrator.exceptions.rest;

import lombok.Getter;

@Getter
public class EditDayRestException extends RuntimeException {
    public EditDayRestException(String message){
        super(message);
    }
}
