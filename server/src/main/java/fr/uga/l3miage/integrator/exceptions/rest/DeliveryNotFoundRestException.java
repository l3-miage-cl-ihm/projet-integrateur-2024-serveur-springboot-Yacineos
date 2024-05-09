package fr.uga.l3miage.integrator.exceptions.rest;

public class DeliveryNotFoundRestException extends EntityNotFoundRestException{
    public DeliveryNotFoundRestException(String message) {
        super(message);
    }
}
