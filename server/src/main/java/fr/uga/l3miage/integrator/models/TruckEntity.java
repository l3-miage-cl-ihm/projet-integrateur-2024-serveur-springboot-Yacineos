package fr.uga.l3miage.integrator.models;

import lombok.*;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class TruckEntity {
    @Id
    private String immatriculation;



}
