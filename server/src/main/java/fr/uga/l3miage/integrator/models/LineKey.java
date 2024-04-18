package fr.uga.l3miage.integrator.models;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class LineKey implements Serializable {

    String orderRef;
    String productRef;
}


