package fr.uga.l3miage.integrator.models;

import fr.uga.l3miage.integrator.dataTypes.Address;
import fr.uga.l3miage.integrator.enums.CustomerState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
@Getter
@SuperBuilder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerEntity {

    @Id
    private String email;

    private String firstName;
    private String lastName;

    private Address address;

    private CustomerState state;



}
