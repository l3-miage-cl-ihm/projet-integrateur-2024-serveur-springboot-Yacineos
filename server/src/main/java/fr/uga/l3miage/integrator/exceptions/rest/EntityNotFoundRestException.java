package fr.uga.l3miage.integrator.exceptions.rest;

import lombok.Getter;


@Getter
public class EntityNotFoundRestException extends RuntimeException{
    public EntityNotFoundRestException(String message) {
        super(message);

    }
}


