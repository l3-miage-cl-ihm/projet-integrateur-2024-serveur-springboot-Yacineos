package fr.uga.l3miage.integrator.dataTypes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@SuperBuilder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String address;
    private String postalCode;
    private String city;
}
