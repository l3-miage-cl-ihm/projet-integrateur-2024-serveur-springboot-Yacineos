package fr.uga.l3miage.integrator.exceptions.rest;

public class DayNotFoundRestException extends EntityNotFoundRestException{
    public DayNotFoundRestException(String message) {
        super(message);
    }
}
