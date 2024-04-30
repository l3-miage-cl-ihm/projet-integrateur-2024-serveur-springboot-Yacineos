package fr.uga.l3miage.integrator.exceptions.rest;

public class TourNotFoundRestException extends EntityNotFoundRestException{
    public TourNotFoundRestException(String message) {
        super(message);
    }
}
