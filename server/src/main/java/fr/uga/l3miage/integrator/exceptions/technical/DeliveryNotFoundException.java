package fr.uga.l3miage.integrator.exceptions.technical;

import lombok.Getter;

@Getter
public class DeliveryNotFoundException extends Exception{
    public DeliveryNotFoundException(String message){
        super(message);
    }
}
