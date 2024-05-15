package fr.uga.l3miage.integrator.exceptions.technical;

import fr.uga.l3miage.integrator.exceptions.rest.WarehouseNotFoundRestException;

public class WarehouseNotFoundException extends WarehouseNotFoundRestException {

    public WarehouseNotFoundException(String message){
        super(message);
    }

}
