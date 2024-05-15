package fr.uga.l3miage.integrator.exceptions.rest;

import fr.uga.l3miage.integrator.enums.DeliveryState;
import lombok.Getter;

@Getter
public class UpdateDeliveryStateRestException extends RuntimeException{
    private final DeliveryState state;
    public UpdateDeliveryStateRestException(String message, DeliveryState state){
        super(message);
        this.state=state;
    }

}
