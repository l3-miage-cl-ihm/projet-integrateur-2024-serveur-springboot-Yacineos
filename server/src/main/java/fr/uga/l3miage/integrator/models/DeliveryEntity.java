package fr.uga.l3miage.integrator.models;

import fr.uga.l3miage.integrator.enums.DeliveryState;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.Set;

@Entity
public class DeliveryEntity {

    @Id
    private String reference;

    private DeliveryState state;
    private double distanceToCover;

    @Column(nullable = true)
    private Integer actualAssemblyTime;
    @Column(nullable = true)
    private LocalTime actualDeliveryTime;

    @OneToMany
    private Set<OrderEntity> orders;

}
