package fr.uga.l3miage.integrator.exceptions.rest;

import fr.uga.l3miage.integrator.exceptions.technical.DataBaseHealthCheckException;
import lombok.Getter;

@Getter
public class HealthcheckRestException extends RuntimeException {
    private final Type type;
    public HealthcheckRestException(String message,Type type) {
        super(message);
        this.type = type;
    }

    public enum Type{
        DATABASE,
        WEB_SERVICE
    }
}
