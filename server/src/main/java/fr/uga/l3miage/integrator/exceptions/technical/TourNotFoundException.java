package fr.uga.l3miage.integrator.exceptions.technical;

import lombok.Getter;


@Getter
public class TourNotFoundException extends  Exception{
    public TourNotFoundException(String message) {
        super(message);

    }
}


