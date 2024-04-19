package fr.uga.l3miage.integrator.models;

import fr.uga.l3miage.integrator.enums.Job;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@SuperBuilder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeEntity {
    @Id
    private String trigram;

    @Column(unique = true)
    private String email;

    private String firstName;
    private String lastName;
    private String photo;

    @Column(unique = true)
    private String mobilePhone;

    private Job job;
}
