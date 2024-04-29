package fr.uga.l3miage.integrator.models;

import fr.uga.l3miage.integrator.enums.TourState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.Set;
import java.util.SortedSet;

@Entity
@Getter
@SuperBuilder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TourEntity {
    @Id
    private String reference;

    private TourState state;
    private String letter;
    private double distanceToCover;

    @Column(nullable = true)
    private Integer actualAssemblyTime ;

    @ManyToMany
    private Set<EmployeeEntity> deliverymen;

    @OneToMany
    @JoinColumn(name = "refTour", referencedColumnName = "reference")
    private Set<DeliveryEntity> deliveries;

    @ManyToOne
    private TruckEntity truck;

}
