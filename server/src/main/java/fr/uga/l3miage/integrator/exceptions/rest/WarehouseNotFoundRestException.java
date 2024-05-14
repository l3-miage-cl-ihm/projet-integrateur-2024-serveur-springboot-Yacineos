package fr.uga.l3miage.integrator.exceptions.rest;

public class WarehouseNotFoundRestException extends EntityNotFoundRestException{
    public WarehouseNotFoundRestException(String message) {
        super(message);
    }

}
