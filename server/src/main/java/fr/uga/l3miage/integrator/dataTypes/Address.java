package fr.uga.l3miage.integrator.dataTypes;

import javax.persistence.Embeddable;

@Embeddable
public class Address {
    private String address;
    private String postalCode;
    private String city;
}
