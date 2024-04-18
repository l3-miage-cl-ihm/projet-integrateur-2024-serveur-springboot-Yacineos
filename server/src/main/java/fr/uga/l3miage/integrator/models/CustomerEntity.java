package fr.uga.l3miage.integrator.models;

import fr.uga.l3miage.integrator.dataTypes.Address;
import fr.uga.l3miage.integrator.enums.CustomerState;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class CustomerEntity {

    @Id
    private String email;

    private String firstName;
    private String lastName;

    private Address address;

    private CustomerState state;


}
