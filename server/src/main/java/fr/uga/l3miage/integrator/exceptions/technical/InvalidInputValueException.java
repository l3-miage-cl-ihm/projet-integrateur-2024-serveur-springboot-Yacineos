package fr.uga.l3miage.integrator.exceptions.technical;

import lombok.Getter;

@Getter

public class InvalidInputValueException extends  Exception{

    public InvalidInputValueException(String message){
        super(message);
    }
}
