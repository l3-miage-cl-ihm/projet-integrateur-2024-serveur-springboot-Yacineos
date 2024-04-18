package fr.uga.l3miage.integrator.models;

import fr.uga.l3miage.integrator.enums.TourState;

import javax.persistence.*;
import java.util.Set;
import java.util.SortedSet;

@Entity
public class TourEntity {
    @Id
    private String reference;

    private TourState state;
    private String letter;
    private double distanceToCover;

    @Column(nullable = true)
    private Integer actualAssemblyTime ;

    @ManyToMany
    private Set<EmployeeEntity> deliveryMen;

    @OneToMany
    private Set<DeliveryEntity> deliveries;
}
