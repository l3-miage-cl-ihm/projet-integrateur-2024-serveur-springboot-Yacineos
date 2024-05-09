package fr.uga.l3miage.integrator.exceptions.technical;

import fr.uga.l3miage.integrator.enums.DeliveryState;
import lombok.Getter;

@Getter
public class UpdateDeliveryStateException extends Exception{
   private final DeliveryState state;
    public UpdateDeliveryStateException(String message,DeliveryState state){
        super(message);
        this.state=state;
    }
}
