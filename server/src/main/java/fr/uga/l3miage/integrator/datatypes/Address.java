package fr.uga.l3miage.integrator.datatypes;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String address;
    private String postalCode;
    private String city;
    @Override
    public String toString(){
        return getAddress()+", "+getCity();
    }
}
